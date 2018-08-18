package com.xyf.commonfx.util;

import com.google.common.base.Preconditions;
import com.xyf.common.annotation.UiThread;
import com.xyf.common.util.FileUtils2;
import com.xyf.common.util.Lg;
import com.xyf.common.util.Sp;
import com.xyf.commonfx.R;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ViewUtils {

    private static final String TAG = ViewUtils.class.getSimpleName();

    private static Window window;
    private static final Sp SP = new Sp(ViewUtils.class);

    public static void init(@Nonnull Window window) {
        ViewUtils.window = window;
    }

    @Nullable
    public static File openDirectory(@Nonnull String saveKey, @Nonnull String title) {
        ensureInit();

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final String KEY_LAST_OPEN_DIRECTORY = String.format("last_open_directory_%s", saveKey);
        directoryChooser.setTitle(title);
        final String record = SP.get(KEY_LAST_OPEN_DIRECTORY);
        if (record != null) {
            final File lastOpenDirectory = new File(record);
            if (FileUtils2.isDirectory(lastOpenDirectory)) {
                directoryChooser.setInitialDirectory(lastOpenDirectory);
            }
        }
        File file = directoryChooser.showDialog(window);
        if (file != null) {
            SP.set(KEY_LAST_OPEN_DIRECTORY, file.getAbsolutePath());
        }

        return file;
    }

    @Nullable
    public static File openFile(@Nonnull String saveKey, @Nonnull String title, @Nonnull String extensionsDescription, @Nonnull String... extensions) {
        ensureInit();

        final String KEY_LAST_OPEN_DIRECTORY = String.format("last_open_file_directory_%s", saveKey);
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionsDescription, extensions));
        final String record = SP.get(KEY_LAST_OPEN_DIRECTORY);
        if (record != null) {
            final File lastOpenDirectory = new File(record);
            if (FileUtils2.isDirectory(lastOpenDirectory)) {
                fileChooser.setInitialDirectory(lastOpenDirectory);
            }
        }
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            SP.set(KEY_LAST_OPEN_DIRECTORY, file.getParentFile().getAbsolutePath());
        }
        return file;
    }

    @Nullable
    public static File saveFile(@Nonnull String saveKey, @Nonnull String title, @Nonnull String extensionsDescription, @Nonnull String... extensions) {
        ensureInit();

        final String KEY_LAST_SAVE_DIRECTORY = String.format("last_save_file_directory_%s", saveKey);
        final String KEY_LAST_SAVE_NAME = String.format("last_save_file_name_%s", saveKey);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionsDescription, extensions));
        final String record = SP.get(KEY_LAST_SAVE_DIRECTORY);
        if (record != null) {
            final File laseSaveDirectory = new File(record);
            if (FileUtils2.isDirectory(laseSaveDirectory)) {
                fileChooser.setInitialDirectory(laseSaveDirectory);
            }
        }
        final String laseSaveName = SP.get(KEY_LAST_SAVE_NAME);
        if (!StringUtils.isEmpty(laseSaveName)) {
            fileChooser.setInitialFileName(laseSaveName);
        }
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            SP.set(KEY_LAST_SAVE_DIRECTORY, file.getParentFile().getAbsolutePath());
            SP.set(KEY_LAST_SAVE_NAME, file.getName());
        }

        return file;
    }

    public static void copyToClipboard(@Nonnull String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent cc = new ClipboardContent();
        cc.putString(string);
        clipboard.setContent(cc);
    }

    private static void ensureInit() {
        Preconditions.checkNotNull(window);
    }

    @UiThread
    public static void showAlert(@Nonnull String title, @Nonnull String content) {
        ensureInit();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(window);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @UiThread
    public static Optional<String> showInputDialog(@Nonnull String saveKey, @Nonnull String content) {
        ensureInit();

        TextInputDialog dialog = new TextInputDialog(SP.get(saveKey, ""));
        dialog.initOwner(window);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText(content);
        dialog.setTitle(null);
        return dialog.showAndWait().map(s -> {
            SP.set(saveKey, s);
            return s;
        });
    }

    @UiThread
    public static void showDialog(@Nonnull String title, @Nonnull String layout) {
        ensureInit();

        Dialog dialog = new Dialog();
        dialog.initOwner(window);
        dialog.setTitle(title);
        final FXMLLoader fxmlLoader = new FXMLLoader(R.getLayout(layout));
        final Parent node;
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            Lg.e(TAG, e);
            return;
        }

        dialog.getDialogPane().setContent(node);
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> Platform.exit());
//        dialog.setOnCloseRequest(event -> Platform.exit());
        dialog.show();
    }

    @UiThread
    public static void exit(int delay) {
        Disposable disposable = Observable.timer(delay, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(aLong -> {
//                    Platform.exit();
                    System.exit(0);
                }, throwable -> Lg.e(TAG, throwable));
    }

    @UiThread
    private static void shake(@Nonnull Window window, long count) {
        ensureInit();

        final double preX = window.getX();
        final double preY = window.getY();
        final double MAX_RANGE_X = 6;
        final double MAX_RANGE_Y = 4;
        final Random random = new Random();
        Disposable disposable = Observable.intervalRange(1, count, 0, 15, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(JavaFxScheduler.platform())
                .doFinally(() -> {
                    window.setX(preX);
                    window.setY(preY);
                })
                .subscribe(aLong -> {
                    window.setX(preX + (random.nextDouble() - 0.5) * MAX_RANGE_X);
                    window.setY(preY + (random.nextDouble() - 0.5) * MAX_RANGE_Y);
                }, throwable -> Lg.e(TAG, throwable));
    }

    @UiThread
    public static void shake() {
        shake(window, 30);
    }

}
