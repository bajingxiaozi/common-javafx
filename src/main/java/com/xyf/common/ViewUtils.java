package com.xyf.common;

import com.xyf.common.util.FileUtils2;
import com.xyf.common.util.Sp;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class ViewUtils {

    private static Window window;
    private static final Sp sp = new Sp(ViewUtils.class);

    public static void init(@Nonnull Window window) {
        ViewUtils.window = window;
    }

    @Nullable
    public static File openDirectory(@Nonnull String title, @Nonnull String tag) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final String KEY_LAST_OPEN_DIRECTORY = "last_open_directory_" + tag;
        directoryChooser.setTitle(title);
        final String record = sp.get(KEY_LAST_OPEN_DIRECTORY);
        if (record != null) {
            final File lastOpenDirectory = new File(record);
            if (FileUtils2.isDirectory(lastOpenDirectory)) {
                directoryChooser.setInitialDirectory(lastOpenDirectory);
            }
        }
        File file = directoryChooser.showDialog(window);
        if (file != null) {
            sp.set(KEY_LAST_OPEN_DIRECTORY, file.getAbsolutePath());
        }

        return file;
    }

    @Nullable
    public static File openFile(@Nonnull String title, @Nonnull String extensionsDescription, @Nonnull String tag, @Nonnull String... extensions) {
        final String KEY_LAST_OPEN_DIRECTORY = "last_open_file_directory_" + tag;
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionsDescription, extensions));
        final String record = sp.get(KEY_LAST_OPEN_DIRECTORY);
        if (record != null) {
            final File lastOpenDirectory = new File(record);
            if (FileUtils2.isDirectory(lastOpenDirectory)) {
                fileChooser.setInitialDirectory(lastOpenDirectory);
            }
        }
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            sp.set(KEY_LAST_OPEN_DIRECTORY, file.getParentFile().getAbsolutePath());
        }
        return file;
    }

    @Nullable
    public static File saveFile(@Nonnull String title, @Nonnull String extensionsDescription, @Nonnull String tag, @Nonnull String... extensions) {
        final String KEY_LAST_SAVE_DIRECTORY = "last_save_file_directory_" + tag;
        final String KEY_LAST_SAVE_NAME = "last_save_file_name_" + tag;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionsDescription, extensions));
        final String record = sp.get(KEY_LAST_SAVE_DIRECTORY);
        if (record != null) {
            final File laseSaveDirectory = new File(record);
            if (FileUtils2.isDirectory(laseSaveDirectory)) {
                fileChooser.setInitialDirectory(laseSaveDirectory);
            }
        }
        final String laseSaveName = sp.get(KEY_LAST_SAVE_NAME);
        if (!StringUtils.isEmpty(laseSaveName)) {
            fileChooser.setInitialFileName(laseSaveName);
        }
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            sp.set(KEY_LAST_SAVE_DIRECTORY, file.getParentFile().getAbsolutePath());
            sp.set(KEY_LAST_SAVE_NAME, file.getName());
        }

        return file;
    }

    public static void copyToClipboard(@Nonnull String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent cc = new ClipboardContent();
        cc.putString(string);
        clipboard.setContent(cc);
    }

}
