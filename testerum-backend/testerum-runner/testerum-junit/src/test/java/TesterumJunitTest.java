import com.testerum.runner.junit5.TesterumJunitTestFactory;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.List;

public class TesterumJunitTest {

    @TestFactory()
    public List<DynamicNode> testerumTests() {
        return new TesterumJunitTestFactory("integration-tests/tests")
            .testPaths("backend/scenarios")
            .getTests();
    }
}
