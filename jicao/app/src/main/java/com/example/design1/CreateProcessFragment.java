package com.example.design1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.design1.PCB;

import java.util.List;

public class CreateProcessFragment extends Fragment {

    private TableLayout tableLayout;
    private List<PCB> pcbList;

    public CreateProcessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 从文件中加载 PCB 数据
        pcbList = PCBFileHelper.loadPCBData(getContext());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_process, container, false);

        // 初始化 TableLayout
        tableLayout = view.findViewById(R.id.tableLayout);


        // 在加载时显示现有的 PCB 列表
        for (PCB pcb : pcbList) {
            addPCBToTable(pcb);
        }

        Button btnCreatePCB = view.findViewById(R.id.btnCreatePCB);
        Button return1 = view.findViewById(R.id.return1);
        btnCreatePCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreatePCBDialog();
            }
        });
        return1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MainActivity.class);
                // 启动跳转
                startActivity(intent);
            }
        });
        return view;
    }

    private void showCreatePCBDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_pcb, null);
        builder.setView(dialogView);

        // 获取对话框中的编辑框
        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtArrivalTime = dialogView.findViewById(R.id.edtArrivalTime);
        EditText edtRunTime = dialogView.findViewById(R.id.edtRunTime);
        EditText edtPriority = dialogView.findViewById(R.id.edtPriority);
        EditText edtNeedP = dialogView.findViewById(R.id.edtNeedP);

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 从编辑框中获取用户输入的信息
                String name = edtName.getText().toString();
                int arrivalTime = Integer.parseInt(edtArrivalTime.getText().toString());
                int runTime = Integer.parseInt(edtRunTime.getText().toString());
                int priority = Integer.parseInt(edtPriority.getText().toString());
                int needP = Integer.parseInt(edtNeedP.getText().toString());

                // 创建新的 PCB 对象并添加到列表中
                PCB pcb = new PCB(name, arrivalTime, runTime, priority, needP);
                pcbList.add(pcb);

                // 将新的 PCB 添加到表格中显示
                addPCBToTable(pcb);

                // 更新文件保存新的 PCB 数据
                PCBFileHelper.savePCBData(getContext(), pcbList);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addPCBToTable(PCB pcb) {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        // 创建 TextView 并设置 PCB 信息
        TextView txtName = new TextView(getContext());
        txtName.setText(pcb.name);
        row.addView(txtName);

        TextView txtArrivalTime = new TextView(getContext());
        txtArrivalTime.setText(String.valueOf(pcb.arrive));
        row.addView(txtArrivalTime);

        TextView txtRunTime = new TextView(getContext());
        txtRunTime.setText(String.valueOf(pcb.needt));
        row.addView(txtRunTime);

        TextView txtPriority = new TextView(getContext());
        txtPriority.setText(String.valueOf(pcb.priority));
        row.addView(txtPriority);

        TextView txtNeedP = new TextView(getContext());
        txtNeedP.setText(String.valueOf(pcb.needp));
        row.addView(txtNeedP);

        TextView txtStatus = new TextView(getContext());
        txtStatus.setText(pcb.status);
        row.addView(txtStatus);

        // 将新的行添加到表格中
        tableLayout.addView(row);
    }
}
