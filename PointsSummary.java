/**
 * To collect all necessary information for grading a student homework using junit test classes, you can
 * <ul>
 *     <li>
 *         either simply let your test class subclass the class {@link JUnitWithPoints}, e.g.
 *  	   <pre>{@code
 *             public class UnitTest extends JUnitWithPoints {
 *                 // ...
 *             }
 *         }</pre>
 *     </li>
 *     <li>
 *         or just copy the two attributes of type {@link PointsLogger} and {@link PointsSummary}
 *         from the class {@link JUnitWithPoints} to your test class instead.
 *         In this case, the variable names are not important, but the annotations {@link org.junit.Rule} resp. {@link org.junit.ClassRule} are!
 *     </li>
 * </ul>
 *
 * @see JUnitWithPoints
 * @see PointsLogger
 */
public final class PointsSummary extends tester.tools.JUnitWithPointsImpl.PointsSummary {
}
