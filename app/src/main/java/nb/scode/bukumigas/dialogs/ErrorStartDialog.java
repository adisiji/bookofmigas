package nb.scode.bukumigas.dialogs;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import nb.scode.bukumigas.R;

/**
 * Created by neobyte on 11/21/2016.
 */

public class ErrorStartDialog extends DialogFragment {

    private MyAlertListener alertListener;

    public interface MyAlertListener{
        void onClickAlert();
    }

    public ErrorStartDialog (){

    }

    public static ErrorStartDialog newInstance(String message) {
        ErrorStartDialog frag = new ErrorStartDialog();
        Bundle args = new Bundle();
        args.putString("msg", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            alertListener = (MyAlertListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.start_alert_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        TextView textView = (TextView)view.findViewById(R.id.message);
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText("ERROR");
        String msg = getArguments().getString("msg","ERROR ALERT");
        textView.setText(msg);
        Button BtnOk = (Button)view.findViewById(R.id.positive_button);

        BtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertListener.onClickAlert();
                dismiss();
            }
        });
        Button exit = (Button)view.findViewById(R.id.close_button);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertListener.onClickAlert();
                dismiss();
            }
        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

}
