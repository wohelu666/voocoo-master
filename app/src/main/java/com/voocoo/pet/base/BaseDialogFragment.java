package com.voocoo.pet.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * Created by chenjiahui on 16/4/6.
 */
public abstract class BaseDialogFragment extends DialogFragment {
    protected DialogFragmentListener mDialogFragmentListener;

    public BaseDialogFragment show(FragmentManager manager) {
        return show(manager, true);
    }

    public BaseDialogFragment show(FragmentManager manager, boolean dismissPrev) {
        return show(manager, BaseDialogFragment.class.getSimpleName(), dismissPrev);
    }

    public BaseDialogFragment show(FragmentManager manager, String tag, boolean dismissPrev) {

        if (dismissPrev && manager != null && manager.getFragments() != null && manager.getFragments().size() != 0) {
            for (Fragment current : manager.getFragments()) {
                if (current == null)
                    continue;
                if (current == this) {
                    break;
                } else if (current instanceof DialogFragment) {
                    DialogFragment df = (DialogFragment) current;
                    df.dismissAllowingStateLoss();
                }
            }
        }

//        super.show(manager, tag);
        if (getActivity() != null && getActivity().isDestroyed()) {
            return this;
        }
        if (!this.isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
        return this;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (mDialogFragmentListener != null) {
            mDialogFragmentListener.onDialogCancel(dialog);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = onCreateBaseView(inflater, container, savedInstanceState);
        if (view != null) {
            getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    onDialogShow(dialog);
                    if (mDialogFragmentListener != null) {
                        mDialogFragmentListener.onDialogShow(dialog);
                    }
                }
            });
        }
        return view;
    }

    public View onCreateBaseView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = onCreateBaseDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                onDialogShow(dialog);
                if (mDialogFragmentListener != null) {
                    mDialogFragmentListener.onDialogShow(dialog);
                }
            }
        });
        return dialog;
    }

    protected void onDialogShow(DialogInterface dialog) {

    }

    public Dialog onCreateBaseDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mDialogFragmentListener != null) {
            mDialogFragmentListener.onDialogDismiss(dialog);
        }
    }

    public BaseDialogFragment setDialogFragmentListener(DialogFragmentListener dialogFragmentListener) {
        mDialogFragmentListener = dialogFragmentListener;
        return this;
    }

    public static class DialogFragmentListener implements DialogInterface.OnClickListener {
        public void onDialogCancel(DialogInterface dialog) {

        }

        public void onDialogDismiss(DialogInterface dialog) {

        }

        public void onDialogConfirm(DialogInterface dialog) {

        }

        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                onDialogConfirm(dialog);
            }
        }

        public void onDialogShow(DialogInterface dialog) {

        }
    }
}
