package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

public class SingleMethodRunner {

	public static void main(String... args) throws ClassNotFoundException {
		if (args.length != 2) {
			System.err.println("Usage: class method FIXME");
			return;
		}

		final String clazz = args[0];
		final String method = args[1];

		final Launcher launcher = LauncherFactory.create();

		final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectMethod(clazz, method))
				.filters(EngineFilter.includeEngines("junit-jupiter"))
				.build();

		launcher.registerTestExecutionListeners(new JUnitRunner.ExecutionTracker());
		launcher.execute(request);

		System.exit(0);
	}
}
