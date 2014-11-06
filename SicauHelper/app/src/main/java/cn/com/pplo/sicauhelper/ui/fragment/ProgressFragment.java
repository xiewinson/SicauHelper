package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.com.pplo.sicauhelper.R;

/**
 * Created by Administrator on 2014/11/6.
 */
public class ProgressFragment extends DialogFragment {

    public static final String EXTRA_DATA = "extra_data";
    public static ProgressFragment newInstance(String text){
        ProgressFragment progressFragment = new ProgressFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DATA, text);
        progressFragment.setArguments(bundle);
        return progressFragment;
    }

    public ProgressFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        String text = getArguments().getString(EXTRA_DATA);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.progress_dialog, null);
        TextView tv = (TextView) view.findViewById(R.id.progress_dialog_tv);
        if(!TextUtils.isEmpty(text)) {
            tv.setText(text);
        }
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    public void show(FragmentManager fragmentManager){
       if(fragmentManager != null) {
           this.show(fragmentManager, null);
       }
    }
}
