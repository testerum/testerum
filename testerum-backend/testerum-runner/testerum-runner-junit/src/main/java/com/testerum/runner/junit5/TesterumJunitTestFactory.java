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
import org.jetbrains.annotations.NotNull;
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

    public @NotNull TesterumJunitTestFactory variablesEnvironment(@NotNull String variablesEnvironment) {
        if (variablesEnvironment == null) throw new IllegalArgumentException("This parameter can not be null");

        this.variablesEnvironment = variablesEnvironment;
        return this;
    }

    public @NotNull TesterumJunitTestFactory variableOverrides(@NotNull Map<String, String> variableOverrides) {
        if (variableOverrides == null) throw new IllegalArgumentException("This parameter can not be null");

        this.variableOverrides = variableOverrides;
        return this;
    }

    public @NotNull TesterumJunitTestFactory settingsFile(@NotNull String settingsFile) {
        if (settingsFile == null) throw new IllegalArgumentException("This parameter can not be null");

        this.settingsFile = Paths.get(settingsFile);
        return this;
    }

    public @NotNull TesterumJunitTestFactory settingOverrides(@NotNull Map<String, String> settingOverrides) {
        if (settingOverrides == null) throw new IllegalArgumentException("This parameter can not be null");

        this.settingOverrides = settingOverrides;
        return this;
    }

    public @NotNull TesterumJunitTestFactory testPaths(@NotNull String... testPaths) {
        if (testPaths == null) throw new IllegalArgumentException("This parameter can not be null");

        this.testPaths = parseStringToTestPath(Arrays.asList(testPaths), repositoryDirectory, "");
        return this;
    }

    public @NotNull TesterumJunitTestFactory tagsToInclude(@NotNull List<String> tagsToInclude) {
        if (tagsToInclude == null) throw new IllegalArgumentException("This parameter can not be null");

        this.tagsToInclude = tagsToInclude;
        return this;
    }

    public @NotNull TesterumJunitTestFactory tagsToExclude(@NotNull List<String> tagsToExclude) {
        if (tagsToExclude == null) throw new IllegalArgumentException("This parameter can not be null");

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
