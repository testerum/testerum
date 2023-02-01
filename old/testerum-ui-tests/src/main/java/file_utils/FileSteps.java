package file_utils;

import com.testerum_api.testerum_steps_api.annotations.steps.Param;
import com.testerum_api.testerum_steps_api.annotations.steps.When;
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator;
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Comparator;

public class FileSteps {

    private final TesterumLogger logger = TesterumServiceLocator.getTesterumLogger();

    @When(value = "I delete the file <<filePath>> from the hard drive",
            description = "Deletes the file or directory from the hard drive")

    public void deleteFile(@Param(description = "File path to be deleted") String filePath) throws IOException {

        try {
        Path pathToBeDeleted = FileSystems.getDefault().getPath(filePath);
        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

        } catch (NoSuchFileException x) {
            System.out.println("no such file or directory: " + filePath.toString());
        }

        logger.info("Deleting file: " + filePath);
    }
}

// try {
//         Path pathToBeDeleted = FileSystems.getDefault().getPath(filePath);
//         Files.walk(pathToBeDeleted)
//         .sorted(Comparator.reverseOrder())
//         .map(Path::toFile)
//         .forEach(File::delete);
//
//         } catch (NoSuchFileException x) {
//         throw new RuntimeException("no such file or directory: " + filePath.toString());
//         }
//
//         logger.info("Deleting file: " + filePath);