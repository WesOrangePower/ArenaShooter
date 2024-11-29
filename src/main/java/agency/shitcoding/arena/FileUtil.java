package agency.shitcoding.arena;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;

import static org.codehaus.plexus.util.FileUtils.copyFileToDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

@SuppressWarnings("unused")
public class FileUtil {

  private static final FileFilter ALLOW_ALL_FILTER = pathname -> true;

  public static Try<Void> tryCopyDirectoryRecursively(
      @NotNull File sourceDir, @NotNull File targetDir) {
    return Try.run(() -> copyDirectoryRecursively(sourceDir, targetDir));
  }

  public static Try<Void> tryCopyDirectoryRecursively(
      @NotNull File sourceDir, @NotNull File targetDir, @NotNull FileFilter fileFilter) {
    return Try.run(() -> copyDirectoryRecursively(sourceDir, targetDir, fileFilter));
  }

  public static void copyDirectoryRecursively(@NotNull File sourceDir, @NotNull File targetDir)
      throws IOException {
    copyDirectoryRecursively(sourceDir, targetDir, targetDir, ALLOW_ALL_FILTER);
  }

  public static void copyDirectoryRecursively(
      @NotNull File sourceDir, @NotNull File targetDir, @NotNull FileFilter fileFilter)
      throws IOException {
    copyDirectoryRecursively(sourceDir, targetDir, targetDir, fileFilter);
  }

  private static void copyDirectoryRecursivelyPreconditions(
      File sourceDirectory, File destinationDirectory) throws IOException {
    if (sourceDirectory == null) {
      throw new IOException("source directory can't be null.");
    }
    if (destinationDirectory == null) {
      throw new IOException("destination directory can't be null.");
    }
    if (sourceDirectory.equals(destinationDirectory)) {
      throw new IOException("source and destination are the same directory.");
    }
    if (!sourceDirectory.exists()) {
      throw new IOException(
          "Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ").");
    }
  }

  private static void copyDirectoryRecursively(
      File sourceDirectory,
      File destinationDirectory,
      File rootDestinationDirectory,
      FileFilter fileFilter)
      throws IOException {
    copyDirectoryRecursivelyPreconditions(sourceDirectory, destinationDirectory);

    if (fileFilter == null) {
      fileFilter = ALLOW_ALL_FILTER;
    }

    File[] files = sourceDirectory.listFiles();
    String sourcePath = sourceDirectory.getAbsolutePath();
    if (files == null) {
      throw new IOException(
          "source directory doesn't exist (" + sourceDirectory.getAbsolutePath() + ").");
    }

    for (File file : files) {
      if (file.equals(rootDestinationDirectory) || !fileFilter.accept(file)) {
        continue;
      }

      String dest = file.getAbsolutePath();
      dest = dest.substring(sourcePath.length() + 1);
      File destination = new File(destinationDirectory, dest);
      if (file.isFile()) {
        destination = destination.getParentFile();
        copyFileToDirectory(file, destination);
      } else {
        if (!file.isDirectory()) {
          throw new IOException("Unknown file type: " + file.getAbsolutePath());
        }

        if (!destination.exists() && !destination.mkdirs()) {
          throw new IOException(
              "Could not create destination directory '" + destination.getAbsolutePath() + "'.");
        }

        copyDirectoryRecursively(file, destination, rootDestinationDirectory, fileFilter);
      }
    }
  }
}
