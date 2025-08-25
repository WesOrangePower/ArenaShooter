package agency.shitcoding.arena.util;

import static agency.shitcoding.arena.ArenaShooter.getMultiverseApi;
import static org.codehaus.plexus.util.FileUtils.copyFileToDirectory;

import agency.shitcoding.arena.ArenaShooter;
import io.vavr.control.Try;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import org.bukkit.World;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class FileUtil {
  private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

  private static final FileFilter ALLOW_ALL_FILTER = pathname -> true;

  public static Try<Void> tryCopyDirectoryRecursively(
      File sourceDir, File targetDir) {
    return Try.run(() -> copyDirectoryRecursively(sourceDir, targetDir));
  }

  public static Try<Void> tryCopyDirectoryRecursively(
      File sourceDir, File targetDir, FileFilter fileFilter) {
    return Try.run(() -> copyDirectoryRecursively(sourceDir, targetDir, fileFilter));
  }

  public static void copyDirectoryRecursively(File sourceDir, File targetDir)
      throws IOException {
    copyDirectoryRecursively(sourceDir, targetDir, targetDir, ALLOW_ALL_FILTER);
  }

  public static void copyDirectoryRecursively(
      File sourceDir, File targetDir, FileFilter fileFilter)
      throws IOException {
    copyDirectoryRecursively(sourceDir, targetDir, targetDir, fileFilter);
  }

  private static void copyDirectoryRecursivelyPreconditions(
      @Nullable File sourceDirectory, @Nullable File destinationDirectory) throws IOException {
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
      @Nullable File sourceDirectory,
      @Nullable File destinationDirectory,
      File rootDestinationDirectory,
      @Nullable FileFilter fileFilter)
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

  public static void deleteWorld(String worldFolderName) {
    getMultiverseApi()
        .ifPresentOrElse(
            mvApi -> {
              try {
              if (!mvApi.getMVWorldManager().deleteWorld(worldFolderName)) {
                deleteWorldFolderForce(worldFolderName);
              }
              } catch (IllegalArgumentException e) {
                deleteWorldFolderForce(worldFolderName);
              }
            },
            () -> deleteWorldFolderForce(worldFolderName));
  }

  public static void deleteWorld(World world) {
    deleteWorld(world.getName());
  }

  private static void deleteWorldFolderForce(String worldFolderName) {
    var root =
        ArenaShooter.getInstance()
            .getDataFolder()
            .toPath()
            .toAbsolutePath()
            .getParent()
            .getParent();

    var worldFolder = root.resolve(worldFolderName).toFile();

    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    String absolutePath = worldFolder.getAbsolutePath();
    try {
      Runtime.getRuntime()
          .exec(
              isWindows
                  ? new String[] {"rmdir", "/s", "/q", absolutePath}
                  : new String[] {"rm", "-rf", absolutePath});
    } catch (Exception e) {
      log.error("Failed to delete world: {}", worldFolder.getName(), e);
    }
  }

  private FileUtil() {}
}
