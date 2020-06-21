import Interop.InteropTests;
import SimpleUnitTest.SimpleUnitTest;

import java.lang.reflect.Method;

/**
 * This tests are run by GitHub Actions.
 *
 * See: .github/workflows/maven.yml
 *
 * @author Tomasz Darmetko
 */
public class AllTests extends SimpleUnitTest {

    public static void main(String[] args) {
        InteropTests.main(args);
        JavaFXTests.main(args);
        testGroups(args);
        testAgentFactories();
    }

    private static void testGroups(String[] args) {
        System.out.println("\n\n\nGroup 3 Tests: \n\n");
        Group3.GroupTests.main(args);
    }

    private static void testAgentFactories() {
        System.out.println("\n\n\nAgentsFactory:\n");
        testAgentsFactory(Group3.AgentsFactory.class);
        testAgentsFactory(GUI.AgentsFactory.class);
    }

    public static void testAgentsFactory(Class<?> factoryClass) {

        String groupName = factoryClass.getPackage().getName();
        it("allows to build agents of " + groupName, () -> {

            boolean executed = false;
            String potentialExceptionMessage = "";

            try {

                Method createIntrudersMethod = factoryClass.getMethod("createIntruders", int.class);
                Method createGuardsMethod = factoryClass.getMethod("createGuards", int.class);
                createIntrudersMethod.invoke(null, 0);
                createGuardsMethod.invoke(null, 0);

                executed = true;

            } catch (Exception e) {
                // Please, review the details yourself.
                potentialExceptionMessage = e.getMessage();
            }

            assertTrue(executed, "Building of agents failed!\n\n" + potentialExceptionMessage);

        });

    }

}
