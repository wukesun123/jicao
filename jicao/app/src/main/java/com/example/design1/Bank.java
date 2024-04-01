package com.example.design1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Bank extends AppCompatActivity {

    int n;
    static int m;//n为进程的个数，m为资源类型的个数
    // 定义资源和进程相关的变量
    private static int maxProcesses = 10;
    private static int maxResources = 10;
    private static int[] available = new int[maxResources];//可利用资源向量
    private int[][] max = new int[maxProcesses][maxResources];//最大需求矩阵
    private static int[][] allocation = new int[maxProcesses][maxResources];//已持有资源矩阵，分配矩阵
    private static int[][] need = new int[maxProcesses][maxResources];//需求矩阵
    private static int[] request = new int[maxResources]; //请求向量
    List<Integer> queue = new ArrayList<>(); // 用于存储安全序列
    private int processCounter = 0; // 添加一个进程计数器

    EditText editTextResourceCount;
    EditText editTextProcessCount;
    EditText SystemRC;
    Button add;
    Button start;
    EditText editName;
    EditText editcount;
    TextView xulie;
    int k ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#283593")));
        init();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String processCountString = editTextProcessCount.getText().toString();
                String ResourceCountString = editTextResourceCount.getText().toString();
                String SystemRCString = SystemRC.getText().toString();
                if (!processCountString.isEmpty() && !ResourceCountString.isEmpty() && !SystemRCString.isEmpty()) {
                    String[] SystemResourceArray = SystemRCString.split(" ");

                    n = Integer.parseInt(processCountString);
                    m = Integer.parseInt(ResourceCountString);
                    if (SystemResourceArray.length != m) {
                        showToast("系统可用资源数填写错误");
                    } else for (int i = 0; i < SystemResourceArray.length; i++) {
                        available[i] = Integer.parseInt(SystemResourceArray[i]);
                    }
                    // 更新表头
                    updateTableHeader();
                    showAddProcessDialog(view);
                } else {
                    showToast("请先填写上述表格");
                }
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editName.getText().toString();
                String s0 = editcount.getText().toString();
                if (!s.isEmpty() && !s0.isEmpty()) {
                    String[] kk = s0.split(" ");
                    if (kk.length != m) {
                        showToast("分配资源种数填写错误");
                    } else {
                        for (int i = 0; i < kk.length; i++) {
                            request[i] = Integer.parseInt(kk[i]);
                        }
                        //算法分配
                        if (k == 0) {
                            if (checkBankQue(available, allocation, need, Integer.parseInt(s))) {
                                allocateRequestResources(Integer.parseInt(s));
                                if (isProcessCompleted(Integer.parseInt(s))){
                                    recoverResources(Integer.parseInt(s));
                                    queue.add(Integer.parseInt(s));
                                }
//                                tanXin();
                                k++;
                            }
                        } else if (k ==2){
                            showToast("已到达最后一步");
                        } else {
                            boolean show = true;
                            for (int i=0;i<n;i++){
                                if (!isProcessCompleted(i))
                                    show = false;
                            }
                            if (!show)
                                tanXin();
                            else{
                                StringBuilder sb = new StringBuilder();
                                sb.append("安全序列为：");
                                int i = 0;
                                for (Integer item : queue) {
                                    if (i!=0){
                                        sb.append("->").append(item); // 将每个整数以空格分隔并添加到字符串中
                                    }else{
                                        sb.append(item); // 将每个整数以空格分隔并添加到字符串中
                                        i++;
                                    }
                                }
                                String sequenceString = sb.toString();
                                xulie.setText(sequenceString); // 将字符串设置到 TextView 中
                                k = 2;
                            }
                        }
                    }
                } else {
                    showToast("请先填写分配资源");
                }
            }
        });


    }

    private void init() {
        editTextResourceCount = findViewById(R.id.editTextResourceCount);
        editTextProcessCount = findViewById(R.id.editTextProcessCount);
        SystemRC = findViewById(R.id.editTextAvailableResource);
        add = findViewById(R.id.btnAddProcess);
        start = findViewById(R.id.btnStartDetection);
        editName = findViewById(R.id.testProcessName);
        editcount = findViewById(R.id.testProcessResource);
        xulie = findViewById(R.id.xulie);
        k = 0;
    }

    //更新表头
    private void updateTableHeader() {
        TableLayout tableLayout = findViewById(R.id.processtableLayout);
        TableRow headerRow = (TableRow) tableLayout.getChildAt(1); // 获取表头行

        // 清除原有的资源命名
        headerRow.removeViews(1, headerRow.getChildCount() - 1); // 保留第一个 TextView，从第二个开始移除

        // 添加新的资源命名
        for (int i = 0; i < 3; i++) {
            TextView textViewResourceName = new TextView(this);
            textViewResourceName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            String s = "";
            for (int j = 0; j < m; j++) {
                char resourceName = (char) ('a' + j); // 使用字符 'a' 加上资源索引 j 来生成资源命名
                s += resourceName + "   "; // 在资源名后添加一个空格
            }
            textViewResourceName.setText(s); // 使用字符 a、b、c... 作为资源命名
            headerRow.addView(textViewResourceName);
        }
    }

    public void showAddProcessDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加进程");

        // 设置对话框布局
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_process, null);
        builder.setView(dialogView);

//        builder.setCancelable(false); // 设置对话框不可取消
        // 设置对话框按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 获取对话框中的输入内容
                EditText editTextMaxResource = dialogView.findViewById(R.id.editTextMaxResource);//
                EditText editTextHoldingResource = dialogView.findViewById(R.id.editTextHoldingResource);


                String maxResource = editTextMaxResource.getText().toString();
                String holdingResource = editTextHoldingResource.getText().toString();

                // 执行安全性检查
                boolean isSafe = checkSafety(maxResource, holdingResource);
                if (isSafe) {
                    addProcessToTable(maxResource, holdingResource);
                } else {
                    showToast("安全性检查未通过");
                    showAddProcessDialog(view);
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean checkSafety(String maxResource, String holdingResource) {
        // 检查输入是否为空
        if (maxResource.isEmpty() || holdingResource.isEmpty()) {
            showToast("输入不能为空");
            return false;
        }

        // 将输入字符串拆分为资源数量数组
        String[] maxResourceArray = maxResource.split(" ");
        String[] holdingResourceArray = holdingResource.split(" ");

        // 检查输入的资源数量是否合法
        if (maxResourceArray.length != m || holdingResourceArray.length != m) {
            showToast("资源数量不对");
            return false;
        }

        // 将字符串数组转换为整数数组
        int[] maxResources = new int[m];
        int[] holdingResources = new int[m];
        try {
            for (int i = 0; i < m; i++) {
                maxResources[i] = Integer.parseInt(maxResourceArray[i]);
                holdingResources[i] = Integer.parseInt(holdingResourceArray[i]);
            }
        } catch (NumberFormatException e) {
            showToast("输入必须为数字");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void addProcessToTable(String maxResource, String holdingResource) {
        TableLayout tableLayout = findViewById(R.id.processtableLayout);
        TableRow row = new TableRow(this);

        // 添加进程名
        TextView textViewProcessName = new TextView(this);


        String[] maxResourceArray = maxResource.split(" ");
        String[] holdingResourceArray = holdingResource.split(" ");
        String[] needResourceArray = calculateNeed(maxResourceArray, holdingResourceArray);
        // 添加最大需求资源
        if (processCounter != n - 1) {
            addmax(maxResourceArray);
            addallocation(holdingResourceArray);
            addneed();
            textViewProcessName.setText(String.valueOf(processCounter++)); // 自动分配进程名
        } else {
            addmax(maxResourceArray);
            addallocation(holdingResourceArray);
            addneed();
            textViewProcessName.setText(String.valueOf(processCounter));
        }

        row.addView(textViewProcessName);

        String temp = "";

        for (String max : maxResourceArray) {
            temp += max + "   ";
        }
        TextView textViewMaxResource = new TextView(this);
        textViewMaxResource.setText(temp);
        row.addView(textViewMaxResource);

        // 添加已持有资源


        String temp1 = "";
        for (String holding : holdingResourceArray) {
            temp1 += holding + "   ";
        }
        TextView textViewHoldingResource = new TextView(this);
        textViewHoldingResource.setText(temp1);
        row.addView(textViewHoldingResource);
        // 计算并添加还需资源


        String temp2 = "";
        for (String n : needResourceArray) {
            temp2 += n + "   ";
        }
        TextView textViewNeedResource = new TextView(this);
        textViewNeedResource.setText(temp2);
        row.addView(textViewNeedResource);
        tableLayout.addView(row);
    }

    private String[] calculateNeed(String[] maxResourceArray, String[] holdingResourceArray) {
        String[] needResourceArray = new String[maxResourceArray.length];
        for (int i = 0; i < maxResourceArray.length; i++) {
            int max = Integer.parseInt(maxResourceArray[i]);
            int holding = Integer.parseInt(holdingResourceArray[i]);
            int n = max - holding;
            needResourceArray[i] = String.valueOf(n);
        }
        return needResourceArray;
    }

    private void addmax(String[] maxResourceArray) {
        for (int i = 0; i < maxResourceArray.length; i++) {
            max[processCounter][i] = Integer.parseInt(maxResourceArray[i]);
        }
    }

    private void addallocation(String[] holding) {
        for (int i = 0; i < holding.length; i++) {
            allocation[processCounter][i] = Integer.parseInt(holding[i]);
        }
    }

    private void addneed() {
        for (int i = 0; i <= processCounter; i++) { // 循环更新所有进程的还需资源
            for (int j = 0; j < m; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
    }

    private void tanXin() {
        for (int i = 0;i<n;i++){
            if (canAllocate(i)){
                if (isProcessCompleted(i)){
                    continue;
                }
                allocateResources(i);
                queue.add(i);
                queue.toString();
                recoverResources(i);
                updataTable();
                break;
            }
        }
    }

    public boolean checkBankQue(int[] available1, int[][] allocation1, int[][] need1, int id) {
        int[] available = Arrays.copyOf(available1,available1.length);
        int[][] allocation = new int[maxProcesses][maxResources];
        int[][] need = new int[maxProcesses][maxResources];
        for (int i = 0;i<n;i++){
            for (int j =0;j<m;j++){
                allocation[i][j] = allocation1[i][j];
                 need[i][j] = need1[i][j];
            }
        }

        List<Integer> safeSequence = new ArrayList<>(); // 用于存储安全序列
        boolean[] finished = new boolean[n]; // 标记进程是否完成
        if (canAllocateRequest(id)) {
            allocateCheckRequestResources(id, available, allocation, need);
            safeSequence.add(id);
            int i = 0;
            while (i < n) {
                for (int j = 0; j < n; j++) {
                    if (!finished[j]&&canAllocateCheck(j,need,available)) {
                        allocateResourcesCheck(j, available, need, allocation);
                        safeSequence.add(j);
                        finished[j] = true;
                        recoverResourcesCheck(j, available, allocation);
                        i++;
                    }
                }
            }
            for (int j = 0;j<n;j++){
                if (!finished[j])
                    return false;
            }
            return true;
        } else showToast("初始分配不合理");
        return false;
    }

    // 更新数据表
    public void updataTable() {
        String s4 = "";
        for (int k = 0; k < m; k++) {
            s4 += available[k] + " ";
        }
        SystemRC.setText(s4);
        for (int i = 0; i < n; i++) {
            TableLayout tableLayout = findViewById(R.id.processtableLayout);// 获取第三行 TableRow
            TableRow Row = (TableRow) tableLayout.getChildAt(i + 2); // 注意索引是从0开始的，第三行索引是2// 获取第三行中的 TextView
            TextView textViewMaxResource = (TextView) Row.getChildAt(1); // 最大需求资源的 TextView
            TextView textViewHoldingResource = (TextView) Row.getChildAt(2); // 已持有资源的 TextView
            TextView textViewNeedResource = (TextView) Row.getChildAt(3); // 还需资源的 TextView\
            String s1 = "";
            String s2 = "";
            String s3 = "";
            for (int j = 0; j < m; j++) {
                s1 += max[i][j] + "    ";
                s2 += allocation[i][j] + "    ";
                s3 += need[i][j] + "    ";
            }
            textViewMaxResource.setText(s1);
            textViewHoldingResource.setText(s2);
            textViewNeedResource.setText(s3);
        }
    }

    // 检查进程是否已完成
    private boolean isProcessCompleted(int id) {
        System.out.println(Collections.singletonList(need[0]));
        for (int i = 0; i < m; i++) {
            if (need[id][i] != 0) {
                System.out.println(need[id][i]);
                System.out.println(i);
                System.out.println(id);
                return false;
            }
        }
        return true;
    }

    private boolean canAllocateRequest(int id) {
        for (int i = 0; i < m; i++) {
            if (request[i] > need[id][i] || request[i] > available[i]) return false;
        }
        return true;
    }

    // 检查是否可以为进程分配资源
    private boolean canAllocate(int id) {
        for (int i = 0; i < m; i++) {
            if (need[id][i] > available[i]) {
                return false;
            }
        }
        return true;
    }
    private boolean canAllocateCheck(int id,int[][]need,int[] available) {
        for (int i = 0; i < m; i++) {
            if (need[id][i] > available[i]) {
                return false;
            }
        }
        return true;
    }

    private void allocateResources(int processId) {
        for (int i = 0; i < m; i++) {
            available[i] -= need[processId][i];
            allocation[processId][i] += need[processId][i];
            need[processId][i] = 0;
        }
    }

    private void allocateResourcesCheck(int processId, int[] available, int[][] need, int[][] allocation) {
        for (int i = 0; i < m; i++) {
            available[i] -= need[processId][i];
            allocation[processId][i] += need[processId][i];
            need[processId][i] = 0;
        }
    }

    private void allocateCheckRequestResources(int processId, int[] available, int[][] allocation, int[][] need) {
        for (int i = 0; i < m; i++) {
            available[i] -= request[i];
            allocation[processId][i] += request[i];
            need[processId][i] -= request[i];
        }
    }

    // 为进程分配资源
    private void allocateRequestResources(int processId) {
        for (int i = 0; i < m; i++) {
            available[i] -= request[i];
            allocation[processId][i] += request[i];
            need[processId][i] -= request[i];
        }
    }

    // 回收进程持有的资源
    private void recoverResources(int processId) {
        for (int i = 0; i < m; i++) {
            available[i] += allocation[processId][i];
            allocation[processId][i] = 0;
        }
    }

    private void recoverResourcesCheck(int processId, int[] available, int[][] allocation) {
        for (int i = 0; i < m; i++) {
            available[i] += allocation[processId][i];
            allocation[processId][i] = 0;
        }
    }

}