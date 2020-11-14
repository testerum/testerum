package com.testerum.runner.junit5;

import com.testerum.model.tests_finder.TestPath;
import com.testerum.runner.junit5.provider.JunitTestProvider;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DynamicNode;

import static com.testerum.runner_cmdline.cmdline.params.parser.TestPathParserKt.parseStringToTestPath;

public class TesterumJunitTestFactory {

    private final Path repositoryDirectory;
    private String variablesEnvironment;
    private Map<String, String> variableOverrides = Collections.emptyMap();
    private Path settingsFile;
    private Map<String, String> settingOverrides = Collections.emptyMap();
    private List<String> packagesToScan = Collections.emptyList();
    private List<TestPath> testPaths = Collections.emptyList();
    private List<String> tagsToInclude = Collections.emptyList();
    private List<String> tagsToExclude = Collections.emptyList();

    public TesterumJunitTestFactory(String repositoryDirectory) {
        this.repositoryDirectory = Paths.get(repositoryDirectory);
    }

    public @NotNull TesterumJunitTestFactory variablesEnvironment(@NotNull String variablesEnvironment) {
        this.variablesEnvironment = variablesEnvironment;
        return this;
    }

    public @NotNull TesterumJunitTestFactory variableOverrides(@NotNull Map<String, String> variableOverrides) {
        this.variableOverrides = variableOverrides;
        return this;
    }

    public @NotNull TesterumJunitTestFactory settingsFile(@NotNull String settingsFile) {
        this.settingsFile = Paths.get(settingsFile);
        return this;
    }

    public @NotNull TesterumJunitTestFactory settingOverrides(@NotNull Map<String, String> settingOverrides) {
        this.settingOverrides = settingOverrides;
        return this;
    }

    public @NotNull TesterumJunitTestFactory packagesToScan(@NotNull List<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
        return this;
    }

    public @NotNull TesterumJunitTestFactory testPaths(@NotNull String... testPaths) {
        this.testPaths = parseStringToTestPath(Arrays.asList(testPaths), repositoryDirectory);
        return this;
    }

    public @NotNull TesterumJunitTestFactory tagsToInclude(@NotNull List<String> tagsToInclude) {
        this.tagsToInclude = tagsToInclude;
        return this;
    }

    public @NotNull TesterumJunitTestFactory tagsToExclude(@NotNull List<String> tagsToExclude) {
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
            packagesToScan,
            testPaths,
            tagsToInclude,
            tagsToExclude
        ).getTesterumTests();
    }
}
