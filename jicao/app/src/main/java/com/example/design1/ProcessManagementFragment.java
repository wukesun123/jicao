package com.example.design1;

// ProcessManagementFragment.java
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

//import com.example.processmanager.R;

public class ProcessManagementFragment extends Fragment {

    Random random = new Random();
    // 生成大于3小于10的随机数
    int S = random.nextInt(10 - 4) + 4;//资源
//    int S = 4;
    Button beging;
    private static List<PCB> pcbList;

    private View rootView;
    private LinearLayout dataContainer;

    public ProcessManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_process_management, container, false);
        dataContainer = rootView.findViewById(R.id.dataContainer);

        beging = rootView.findViewById(R.id.btnProcessPCB);
        pcbList = PCBFileHelper.loadPCBData(getContext());
        beging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PSA(pcbList,rootView);
            }
        });
        Button return2 = rootView.findViewById(R.id.return2);
        return2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MainActivity.class);
                // 启动跳转
                startActivity(intent);
            }
        });

        return rootView;
    }
    public void PSA(List<PCB> pcbList,View view){
        PriorityQueue<PCB> readyQueue = new PriorityQueue<>();  // 就绪队列
        PriorityQueue<PCB> waitingQueue = new PriorityQueue<>();  // 等待队列
        PriorityQueue<PCB> finishedQueue = new PriorityQueue<>();  // 完成队列
        for (PCB pcb : pcbList) {
            readyQueue.add(pcb);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 *要执行的操作
                 */
                int currentTime = 0;
                if (!readyQueue.isEmpty() || !waitingQueue.isEmpty()) {
                    while (!readyQueue.isEmpty()) {
                        PCB currentProcess = readyQueue.poll();
                        if(currentProcess.arrive !=currentTime){
                            readyQueue.add(currentProcess);
                            currentTime++;
                        }else{
                            currentTime++;
                            // 分配资源
                            if (allocateResources(currentProcess)) {
                                // 执行一个时间片
                                //状态切换显示一下
                                showPCB(pcbList, readyQueue, waitingQueue, finishedQueue, view);
                                currentProcess.setUsed(currentProcess.getUsed() + 1);
                                // 若已用 CPU 时间等于需要的运行时间，则放入完成队列
                                if (currentProcess.getUsed() == currentProcess.getNeedt()) {
                                    currentProcess.setStatus("F"); // 完成状态
                                    finishedQueue.add(currentProcess);
                                    releaseResources(currentProcess); // 释放资源
                                    showPCB(pcbList, readyQueue, waitingQueue, finishedQueue, view);
                                } else {
                                    currentProcess.setStatus("R"); // 就绪状态
                                    if (currentProcess.priority != 1)
                                        currentProcess.setPriority(currentProcess.getPriority() - 1); // 降低优先级
                                    readyQueue.add(currentProcess);
                                    showPCB(pcbList, readyQueue, waitingQueue, finishedQueue, view);
                                }
                                // 更新界面
                                showPCB(pcbList, readyQueue, waitingQueue, finishedQueue, view);
                            } else {
                                currentProcess.setStatus("W"); // 等待状态
                                waitingQueue.add(currentProcess);
                                showPCB(pcbList, readyQueue, waitingQueue, finishedQueue, view);
                            }
                            // 尝试唤醒等待队列中的进程
                            while (!waitingQueue.isEmpty()) {
                                PCB waitingProcess = waitingQueue.peek();
                                if (waitingProcess.getNeedp() <= S) {
                                    waitingQueue.poll();  // 从等待队列中移除
                                    waitingProcess.setStatus("R"); // 就绪状态
                                    readyQueue.add(waitingProcess);  // 加入就绪队列
                                } else {
                                    break;  // 无法满足后续等待队列中的进程，退出循环
                                }
                            }

                            // 再次执行 PSA
                            handler.postDelayed(this, 1000);
                            return; // 退出循环
                        }

                    }
                }else {
                    // 逻辑结束后添加“结束模拟”按钮
                    Button endSimulationButton = new Button(getContext());
                    endSimulationButton.setText("结束模拟");
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    int margin = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
                    params.setMargins(0, 0, 0, margin);
                    endSimulationButton.setLayoutParams(params);
                    ((RelativeLayout) view).addView(endSimulationButton);
                    // 设置“结束模拟”按钮的点击事件
                    endSimulationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PCBFileHelper.deletePCBDataFile(getContext());
                        }
                    });
                }
            }

        }, 1000);
    }

    void showPCB(List<PCB> pcbList, Queue<PCB> readyQueue, Queue<PCB> waitQueue, Queue<PCB> finishedQueue, View view){
        dataContainer.removeAllViews(); // 清空数据容器

        // 创建表头
        TextView headerText = new TextView(getContext());
        headerText.setText("进程状态：");
        dataContainer.addView(headerText);
        addSpacer(50);
        // 添加表格表头
        TextView tableHeader = new TextView(getContext());
        tableHeader.setText("Name\t\t\tArrival\t\t\tNeedT\t\t\tPriority\t\t\tUsedT\t\t\tNeedP\t\t\tState");
        dataContainer.addView(tableHeader);

        // 创建表格
        for (PCB pcb : pcbList) {
            TextView pcbData = new TextView(getContext());
            pcbData.setText(String.format("%s\t\t\t\t\t\t\t%d\t\t\t\t\t\t\t%d\t\t\t\t\t\t\t%d\t\t\t\t\t\t\t%d\t\t\t\t\t\t\t%d\t\t\t\t\t\t\t%s",
                    pcb.getName(), pcb.getArrive(), pcb.getNeedt(), pcb.getPriority(), pcb.getUsed(), pcb.getNeedp(), pcb.getStatus()));
            dataContainer.addView(pcbData);
        }
        // 添加间隔
        addSpacer(100);
        // 显示就绪队列
        TextView readyQueueText = new TextView(getContext());
        readyQueueText.setText("就绪队列：" + getQueueNames(readyQueue));
        dataContainer.addView(readyQueueText);
        // 添加间隔
        addSpacer(50);
        // 显示等待队列
        TextView waitQueueText = new TextView(getContext());
        waitQueueText.setText("等待队列：" + getQueueNames(waitQueue));
        dataContainer.addView(waitQueueText);
        // 添加间隔
        addSpacer(50);
        // 显示完成队列
        TextView finishedQueueText = new TextView(getContext());
        finishedQueueText.setText("完成队列：" + getQueueNames(finishedQueue));
        dataContainer.addView(finishedQueueText);
    }
    private static String getQueueNames(Queue<PCB> queue) {
        StringBuilder names = new StringBuilder();
        for (PCB pcb : queue) {
            names.append(pcb.getName()).append(" ");
        }
        return names.toString().trim();
    }
    // 分配资源
    private boolean allocateResources(PCB pcb) {
        if (pcb.isAlcate()){
            return true;
        }else if (S>=pcb.needp) {
                S -= pcb.needp;
                pcb.setAlcate(true);
                pcb.setStatus("E"); // 已获取资源，进入执行状态
                return true;
            }
        return false;
    }

    // 释放资源
    private void releaseResources(PCB pcb) {
        S += pcb.needp;
    }
    // 添加间隔方法
    // 添加间隔方法
    private void addSpacer(int height) {
        View spacer = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        spacer.setLayoutParams(params);
        dataContainer.addView(spacer);
    }
}

