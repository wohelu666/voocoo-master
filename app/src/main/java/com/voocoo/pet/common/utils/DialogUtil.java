package com.voocoo.pet.common.utils;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.berwin.cocoadialog.CocoaDialog;
import com.berwin.cocoadialog.CocoaDialogAction;
import com.berwin.cocoadialog.CocoaDialogActionStyle;
import com.berwin.cocoadialog.CocoaDialogStyle;


/**
 * Created by sunny on 6/2/18.
 */
public class DialogUtil {

    public static CocoaDialog alertSingleBtn(@NonNull Context context, String titleRes,
                                             String messageRes, String buttonTitleRes) {
        return alert(context, true, titleRes, messageRes, buttonTitleRes, null);
    }

    public static CocoaDialog alert(@NonNull Context context, @StringRes int titleRes,
                                    @StringRes int messageRes, @StringRes int buttonTitleRes) {
        return alert(context, true, titleRes, messageRes, buttonTitleRes, 0, null, null);
    }

    public static CocoaDialog alert(@NonNull Context context, @StringRes int titleRes,
                                    @StringRes int messageRes, @StringRes int buttonTitleRes,
                                    CocoaDialogAction.OnClickListener buttonClickListener) {
        return alert(context, true, titleRes, messageRes, buttonTitleRes, 0, buttonClickListener, null);
    }

    public static CocoaDialog alert(@NonNull Context context, boolean cancelable, @StringRes int titleRes,
                                    @StringRes int messageRes, @StringRes int buttonTitleRes,
                                    CocoaDialogAction.OnClickListener buttonClickListener) {
        return alert(context, cancelable, titleRes, messageRes, buttonTitleRes, 0, buttonClickListener, null);
    }

    public static CocoaDialog alert(
            @NonNull Context context,
            @StringRes int titleRes, @StringRes int messageRes,
            @StringRes int button1TitleRes, @StringRes int button2TitleRes,
            CocoaDialogAction.OnClickListener button1ClickListener,
            CocoaDialogAction.OnClickListener button2ClickListener) {
        return alert(context, true, titleRes, messageRes,
                button1TitleRes, button2TitleRes, button1ClickListener, button2ClickListener);
    }


    public static CocoaDialog alert(
            @NonNull Context context,
            String title, String message,
            String button1Title, String button2Title,
            CocoaDialogAction.OnClickListener button1ClickListener,
            CocoaDialogAction.OnClickListener button2ClickListener) {
        return alert(context, true, title, message, button1Title, button2Title, button1ClickListener, button2ClickListener);
    }

    public static CocoaDialog alert(
            @NonNull Context context, boolean cancelable,
            @StringRes int titleRes, @StringRes int messageRes,
            @StringRes int button1TitleRes, @StringRes int button2TitleRes,
            CocoaDialogAction.OnClickListener button1ClickListener,
            CocoaDialogAction.OnClickListener button2ClickListener) {
        String title = context.getString(titleRes);
        String message = context.getString(messageRes);
        String btn1Title = button1TitleRes != 0 ? context.getString(button1TitleRes) : null;
        String btn2Title = button2TitleRes != 0 ? context.getString(button2TitleRes) : null;
        return alert(context, cancelable, title, message, btn1Title, btn2Title, button1ClickListener, button2ClickListener);
    }

    public static CocoaDialog alert(
            @NonNull Context context,
            boolean cancelable,
            String title, String message,
            String button1Title,
            CocoaDialogAction.OnClickListener button1ClickListener) {
        CocoaDialog.Builder builder = new CocoaDialog.Builder(context, CocoaDialogStyle.alert)
                .setTitle(title).setMessage(message).setCancelable(cancelable);
        if (button1Title != null || button1ClickListener != null) {
            builder.addAction(new CocoaDialogAction(
                    button1Title,
                    CocoaDialogActionStyle.normal,
                    button1ClickListener));
        }
        return builder.build();
    }

    public static CocoaDialog alert(
            @NonNull Context context,
            boolean cancelable,
            String title, String message,
            String button1Title, String button2Title,
            CocoaDialogAction.OnClickListener button1ClickListener,
            CocoaDialogAction.OnClickListener button2ClickListener) {
        CocoaDialog.Builder builder = new CocoaDialog.Builder(context, CocoaDialogStyle.alert)
                .setTitle(title).setMessage(message).setCancelable(cancelable);
        if (button1Title != null || button1ClickListener != null) {
            builder.addAction(new CocoaDialogAction(
                    button1Title,
                    CocoaDialogActionStyle.normal,
                    button1ClickListener));
        }
        if (button2Title != null || button2ClickListener != null) {
            builder.addAction(new CocoaDialogAction(
                    button2Title,
                    CocoaDialogActionStyle.normal,
                    button2ClickListener));
        }
        return builder.build();
    }

    public static CocoaDialog actionSheet(
            @NonNull Context context, @StringRes int titleRes,
            @StringRes int messageRes, @StringRes int cancelRes,
            @StringRes int button1TitleRes, @StringRes int button2TitleRes,
            CocoaDialogAction.OnClickListener cancelClickListener,
            CocoaDialogAction.OnClickListener button1ClickListener,
            CocoaDialogAction.OnClickListener button2ClickListener) {
        CocoaDialog.Builder builder = new CocoaDialog.Builder(context, CocoaDialogStyle.actionSheet);
        builder.setTitle(titleRes);
        builder.setMessage(messageRes);
        if (cancelRes != 0) {
            builder.addAction(new CocoaDialogAction(
                    context.getString(cancelRes),
                    CocoaDialogActionStyle.cancel,
                    cancelClickListener));
        }
        if (button1TitleRes != 0) {
            builder.addAction(new CocoaDialogAction(
                    context.getString(button1TitleRes),
                    CocoaDialogActionStyle.normal,
                    button1ClickListener
            ));
        }
        if (button2TitleRes != 0) {
            builder.addAction(new CocoaDialogAction(
                    context.getString(button2TitleRes),
                    CocoaDialogActionStyle.normal,
                    button2ClickListener
            ));
        }
        return builder.build();
    }

//    public static CocoaDialog loading(Context context) {
//        return context == null ? null : loading(context, false);
//    }
//
//    public static CocoaDialog loading(@NonNull Context context, boolean cancelable) {
//        return new CocoaDialog.Builder(context, CocoaDialogStyle.custom)
//                .setCustomContentView(LayoutInflater.from(context).inflate(R.layout.dialog_loading, null))
//                .setCancelable(cancelable).build();
//    }
}
