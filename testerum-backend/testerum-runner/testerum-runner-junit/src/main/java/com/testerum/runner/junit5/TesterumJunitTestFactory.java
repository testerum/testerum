package com.testerum.runner.junit5;

import static com.testerum.runner_cmdline.cmdline.params.parser.TestPathParserKt.parseStringToTestPath;

import com.testerum.model.tests_finder.TestPath;
import com.testerum.runner.junit5.provider.JunitTestProvider;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DynamicNode;

public class TesterumJunitTestFactory {

    private final Path repositoryDirectory;
    private String variablesEnvironment;
    private Map<String, String> variableOverrides = Collections.emptyMap();
    private Path settingsFile;
    private Map<String, String> settingOverrides = Collections.emptyMap();
    private List<TestPath> testPaths = Collections.emptyList();
    private List<String> tagsToInclude = Collections.emptyList();
    private List<String> tagsToExclude = Collections.emptyList();

    public TesterumJunitTestFactory(String repositoryDirectory) {
        this.repositoryDirectory = Paths.get(repositoryDirectory);
    }

    public TesterumJunitTestFactory setVariablesEnvironment(String variablesEnvironment) {
        this.variablesEnvironment = variablesEnvironment;
        return this;
    }

    public TesterumJunitTestFactory variableOverrides(Map<String, String> variableOverrides) {
        if (variableOverrides == null) return this;

        this.variableOverrides = variableOverrides;
        return this;
    }

    public TesterumJunitTestFactory settingsFile(String settingsFile) {
        this.settingsFile = Paths.get(settingsFile);
        return this;
    }

    public TesterumJunitTestFactory settingOverrides(Map<String, String> settingOverrides) {
        if (settingOverrides == null) return this;

        this.settingOverrides = settingOverrides;
        return this;
    }

    public TesterumJunitTestFactory testPaths(String... testPaths) {
        if (testPaths == null) return this;

        this.testPaths = parseStringToTestPath(Arrays.asList(testPaths), repositoryDirectory, "");
        return this;
    }

    public TesterumJunitTestFactory tagsToInclude(List<String> tagsToInclude) {
        if (tagsToInclude == null) return this;

        this.tagsToInclude = tagsToInclude;
        return this;
    }

    public TesterumJunitTestFactory tagsToExclude(List<String> tagsToExclude) {
        if (tagsToExclude == null) return this;

        this.tagsToExclude = tagsToExclude;
        return this;
    }

    public List<DynamicNode> getTests() {
        return new JunitTestProvider(
            repositoryDirectory,
            variablesEnvironment,
            variableOverrides,
            settingsFile,
            settingOverrides,
            testPaths,
            tagsToInclude,
            tagsToExclude
        ).getTesterumTests();
    }
}
