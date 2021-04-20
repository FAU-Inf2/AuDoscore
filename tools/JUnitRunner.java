package tools;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

public class JUnitRunner {

	static class TestFailure {
		private final String source;
		private final Throwable cause;

		TestFailure(final String source, final Throwable cause) {
			this.source = source;
			this.cause = cause;
		}

		public String getSource() {
			return this.source;
		}

		public Throwable getCause() {
			return this.cause;
		}
	}

	static class ExecutionTracker implements TestExecutionListener {

		private long startTime;
		private int numTests;
		private List<TestFailure> failures;
		private final PrintStream outStream = System.out;
		private final PrintStream errStream = System.err;

		private static String getSource(final TestIdentifier testIdentifier) {
			if (testIdentifier.getSource().isPresent()) {
				if (testIdentifier.getSource().get() instanceof MethodSource) {
					final MethodSource methodSource = (MethodSource) testIdentifier.getSource().get();
					return methodSource.getMethodName() + "(" + methodSource.getClassName() + ")";
				}
			}
			return "???";
		}

		@Override
		public void dynamicTestRegistered(TestIdentifier testIdentifier) {
		}

		@Override
		public void executionFinished(TestIdentifier testIdentifier,
				TestExecutionResult testExecutionResult) {

			if (testIdentifier.getType() == TestDescriptor.Type.TEST) {
				this.numTests += 1;

				switch (testExecutionResult.getStatus()) {
					case ABORTED:
						this.outStream.print("A");
						break;

					case SUCCESSFUL:
						this.outStream.print(".");
						break;

					default:
						this.outStream.print("E");
						this.failures.add(new TestFailure(
								getSource(testIdentifier),
								testExecutionResult.getThrowable().get()));
						break;
				}
			} else if (this.numTests > 0) {
				final long endTime = System.nanoTime();
				this.outStream.printf("%nTime: %.3f%n", (endTime - this.startTime) / 1000000000.0);

				if (!this.failures.isEmpty()) {
					if (this.failures.size() == 1) {
						this.outStream.println("There was 1 failure:");
					} else {
						this.outStream.println("There were " + this.failures.size() + " failures:");
					}

					for (int i = 0; i < this.failures.size(); ++i) {
						this.outStream.println((i + 1) + ") " + this.failures.get(i).getSource());
						this.failures.get(i).getCause().printStackTrace(this.outStream);
						this.outStream.println();
					}

					this.outStream.println("FAILURES!!!");
					this.outStream.println("Tests run: " + this.numTests
							+ ",  Failures: " + this.failures.size());
				} else {
					if (this.numTests == 1) {
						this.outStream.println("OK (1 test)");
					} else {
						this.outStream.println("OK (" + this.numTests + " tests)");
					}
				}

				this.numTests = 0;
			} else if (testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED) {
				// Enforce an internal error
				testExecutionResult.getThrowable().get().printStackTrace(this.errStream);
			}
		}

		@Override
		public void executionSkipped(TestIdentifier testIdentifier, String reason) {
		}

		@Override
		public void executionStarted(TestIdentifier testIdentifier) {
			if (testIdentifier.getType() == TestDescriptor.Type.CONTAINER) {
				this.startTime = System.nanoTime();
				this.numTests = 0;
				this.failures = new ArrayList<>();
			}
		}

		@Override
		public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
		}

		@Override
		public void testPlanExecutionFinished(TestPlan testPlan) {
		}

		@Override
		public void testPlanExecutionStarted(TestPlan testPlan) {
		}
	}

	public static void main(String... args) throws ClassNotFoundException {

		final Launcher launcher = LauncherFactory.create();

		final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(
					Arrays.stream(args)
						.map(DiscoverySelectors::selectClass)
						.toArray(DiscoverySelector[]::new))
				.filters(EngineFilter.includeEngines("junit-jupiter"))
				.build();

		launcher.registerTestExecutionListeners(new JUnitRunner.ExecutionTracker());
		launcher.execute(request);

		System.exit(0);
	}
}

