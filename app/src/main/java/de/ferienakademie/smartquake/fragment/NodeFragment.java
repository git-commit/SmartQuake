package de.ferienakademie.smartquake.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.view.CanvasView;

/**
 * Created by yuriy on 27/09/16.
 */
public class NodeFragment extends DialogFragment {

    public interface NodeParametersListener {
        public void onChangeNode();
    }

    Node node = null;

    NodeParametersListener caller;

    public void setNode(Node node) { this.node = node; }
    public void setListener(NodeParametersListener caller) { this.caller = caller; }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
        LayoutInflater i = getActivity().getLayoutInflater();

        final View view = i.inflate(R.layout.choose_node, null);

        final Switch isHingeButton = (Switch)view.findViewById(R.id.is_hinge);
        if (node.isHinge()) isHingeButton.toggle();

        final EditText massText = (EditText)view.findViewById(R.id.node_mass);
        massText.setHint(String.valueOf(node.getNodeMass()));

        final CheckBox xConst = (CheckBox)view.findViewById(R.id.checkBox_x);
        final CheckBox yConst = (CheckBox)view.findViewById(R.id.checkBox_y);
        final CheckBox rConst = (CheckBox)view.findViewById(R.id.checkBox_rot);

        xConst.setChecked(node.getConstraint(0));
        yConst.setChecked(node.getConstraint(1));
        rConst.setChecked(node.getConstraint(2));


        bob.setView(view)
                .setMessage("Node parameters")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        node.setHinge(isHingeButton.isChecked());
                        if (!massText.getText().toString().isEmpty())
                            node.setNodeMass(Double.parseDouble(massText.getText().toString()));
                        boolean[] constraints = new boolean[] {xConst.isChecked(), yConst.isChecked(), rConst.isChecked()};
                        node.setConstraint(constraints);
                        caller.onChangeNode();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return bob.create();
    }

}