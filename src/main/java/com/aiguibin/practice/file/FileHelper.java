package com.aiguibin.practice.file;

import java.io.*;
import java.util.Scanner;

public class FileHelper {

    /**
     * 字节流文件的拷贝
     * @return 是否成功拷贝
     */
    public static boolean copyFile_byte(){

        File file= null;
        InputStream inputStream=null;
        OutputStream outputStream=null;
        try {
            //创建文件对象
            file = new File("G:\\ProLogs\\aiguibin-common-core.log");
            //文件输入流转输入流
            inputStream=new FileInputStream(file);
            //创建容器字节数组,大小为文件大小
            byte[] bytes =new byte[(int) file.length()];
            //输出流读取到容器,返回读取到的内容大小
            int readLength = inputStream.read(bytes);
            System.out.println(readLength);
            //把容器中的输入流转换成字符串
            String s = new String(bytes);
            System.out.println(s);
            //把字符串转成字节数组
            byte[] byteStr = s.getBytes();
            //创建文件输出流
            outputStream=new FileOutputStream("G:\\ProLogs\\aiguibin-common-core-copy.log",true);
            //把字节数组写入到文件
            outputStream.write(byteStr);
           /** // 创建
            BufferedInputStream bis= new BufferedInputStream(inputStream);
            int i = bis.read(bytes);
            System.out.println(i);
            BufferedOutputStream bos=new BufferedOutputStream(outputStream);
            bos.write(bytes);*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();

            } catch (IOException e) {
            }
        }
        return true;
    }

    /**
     * 字符流，缓冲流的文件拷贝
     *
     * @return 是否成功
     */

    public static boolean copeFile_buffer() {
        try {
            Reader reader=new FileReader("G:\\ProLogs\\aiguibin-common-core.log");
            BufferedReader br=new BufferedReader(reader);
            StringBuffer sb=new StringBuffer();
            String str="";
            while ((str=br.readLine())!=null){
                sb=sb.append(str+"\n");
            }
            str=sb.toString();
            System.out.println(str);
            reader.close();
            br.close();
            //写入
            Writer writer= new FileWriter("G:\\ProLogs\\aiguibin-common-core-copy.log");
            BufferedWriter bw= new BufferedWriter(writer);
            bw.write(str);
            bw.flush();
            bw.close();
            //写入
            PrintWriter pw=new PrintWriter(new FileOutputStream("G:\\ProLogs\\aiguibin-common-core-copy.log"));
            pw.print("PrintWriter pw");
            pw.flush();
            pw.close();
            //读取
            Scanner scanner= new Scanner(new FileInputStream("G:\\ProLogs\\aiguibin-common-core.log"));
            StringBuffer bf=new StringBuffer();
            while (scanner.hasNext()){
                bf=bf.append(scanner.nextLine()+"\n");
            }
            System.out.println(bf);
            scanner.close();

            Scanner sc=new Scanner("我是中国人 , 我要换行输出");
            // 默认是空格分割 如果不写就会自动按照空格换行
            // sc.useDelimiter(",");
            while (sc.hasNext()){
                System.out.println(sc.next());
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return true;
    }

    /**
     * 主函数
     * @param args
     */
    public static void main(String[] args) {
        // copyFile_byte();
        // copeFile_buffer();
    }

}
