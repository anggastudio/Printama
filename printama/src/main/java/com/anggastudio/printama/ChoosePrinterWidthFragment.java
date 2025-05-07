package com.anggastudio.printama;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class ChoosePrinterWidthFragment extends DialogFragment {

    private Printama.OnChoosePrinterWidth onChoosePrinterWidth;
    private Button saveButton;
    ImageView iv2inchSelected;
    ImageView iv3inchSelected;
    private int inactiveColor;
    private int activeColor;
    private boolean is3inches;

    public ChoosePrinterWidthFragment() {
        // Required empty public constructor
    }

    public static ChoosePrinterWidthFragment newInstance() {
        ChoosePrinterWidthFragment fragment = new ChoosePrinterWidthFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_printer_width, container, false);
    }

    public void setOnChoosePrinterWidth(Printama.OnChoosePrinterWidth onChoosePrinterWidth) {
        this.onChoosePrinterWidth = onChoosePrinterWidth;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveButton = view.findViewById(R.id.btn_save);
        saveButton.setOnClickListener(v -> savePrinterWidth());

        iv2inchSelected = view.findViewById(R.id.iv_select_width_2_inches);
        iv3inchSelected = view.findViewById(R.id.iv_select_width_3_inches);

        // default data
        is3inches = Pref.getBoolean(Pref.IS_PRINTER_3INCH);
        if (is3inches) {
            select3inches();
        } else {
            select2inches();
        }

        // default view
        defaultSelectorView(view);
    }

    private void defaultSelectorView(View view) {
        View layout2inch = view.findViewById(R.id.layout_printer_width_item_1);
        View layout3inch = view.findViewById(R.id.layout_printer_width_item_2);

        layout2inch.setOnClickListener(v -> {
            select2inches();
        });

        layout3inch.setOnClickListener(v -> {
            select3inches();
        });

    }

    private void select2inches() {
        is3inches = false;
        iv2inchSelected.setImageResource(R.drawable.ic_check_circle);
        iv3inchSelected.setImageResource(R.drawable.ic_circle);
    }

    private void select3inches() {
        is3inches = true;
        iv2inchSelected.setImageResource(R.drawable.ic_circle);
        iv3inchSelected.setImageResource(R.drawable.ic_check_circle);
    }

    private void savePrinterWidth() {
        Printama.is3inchesPrinter(is3inches);
        if (onChoosePrinterWidth != null) {
            onChoosePrinterWidth.onChoosePrinterWidth(is3inches);
        }
        dismiss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setColor();
    }

    private void setColor() {
        if (getContext() != null) {
            if (this.activeColor == 0) {
                this.activeColor = ContextCompat.getColor(getContext(), R.color.colorGreen);
            }
            if (this.inactiveColor == 0) {
                this.inactiveColor = ContextCompat.getColor(getContext(), R.color.colorGray5);
            }
        }

    }

    public void setColorTheme(int activeColor, int inactiveColor) {
        if (activeColor != 0) {
            this.activeColor = activeColor;
        }
        if (inactiveColor != 0) {
            this.inactiveColor = inactiveColor;
        }
    }
}
