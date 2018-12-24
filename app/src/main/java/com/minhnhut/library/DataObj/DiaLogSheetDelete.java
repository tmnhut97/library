package com.minhnhut.library.DataObj;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minhnhut.library.R;

public class DiaLogSheetDelete extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.dialog_sheet_delete,container,false);
        return v;
    }
//    @Override
//    public void setupDialog(Dialog dialog, int style) {
//        super.setupDialog(dialog, style);
//        View view = View.inflate(getContext(), R.layout.dialog_sheet_delete, null);
//        dialog.setContentView(view);
//    }
}
