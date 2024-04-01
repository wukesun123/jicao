package com.example.design1;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class PCBFileHelper {
    private static final String FILENAME = "pcb_data.txt";
    // 保存 PCB 数据到文件
    public static void savePCBData(Context context, List<PCB> pcbList) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            for (PCB pcb : pcbList) {
                writer.write(pcb.toCSV());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 从文件中加载 PCB 数据
    public static List<PCB> loadPCBData(Context context) {
        List<PCB> pcbList = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String name = parts[0];
                    int arrive = Integer.parseInt(parts[1]);
                    int needt = Integer.parseInt(parts[2]);
                    int priority = Integer.parseInt(parts[3]);
                    int needp = Integer.parseInt(parts[4]);
                    String status = parts[5];
                    PCB pcb = new PCB(name, arrive, needt, priority, needp);
                    pcb.status = status;
                    pcbList.add(pcb);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pcbList;
    }
    // 删除 PCB 数据文件
    public static void deletePCBDataFile(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILENAME);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

