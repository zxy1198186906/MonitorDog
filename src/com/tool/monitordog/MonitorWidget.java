package com.tool.monitordog;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.*;

public class MonitorWidget {
	
	private static FileUtil fUtil = FileUtil.getInstance();
	private static ConvertUtil cUtil = ConvertUtil.getInstance();
	private static String PATH = "/watchdog/memAnalysisFull.txt";
	
	private static boolean startResult;
	private static boolean reportResult;
	
	public static void main(String[] args) {
		JFrame mFrame = new JFrame("Monitor Dog");
		JPanel mPanel = new JPanel();
		
		mFrame.setSize(1300, 800);
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.add(mPanel);
		
		placeComponents(mPanel);
		mFrame.setVisible(true);	
	}
	
	private static String placeOutput(String mString) {
		SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date mDate = new Date();
		return mFormat.format(mDate)+"      "+mString+"\n";
	}
	
	
	private static void placeComponents(JPanel mPanel) {
		mPanel.setLayout(null);
		
		JLabel mLabel = new JLabel("监测应用包名: ");
		JLabel tLabel = new JLabel("监测时间(秒):");
		JTextField mText = new JTextField(30);
		JTextField mTime = new JTextField(8);
		JButton startBtn = new JButton("开始分析");
		JButton reportBtn = new JButton("生成报告");
		JButton evalBtn = new JButton("性能评价");
		
		JTextArea mLog = new JTextArea();
		JScrollPane mScroll = new JScrollPane(mLog);
		mScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//mLog.setCaretPosition(mLog.getText().length());
		
		mLog.setEditable(false);
		mLog.setLineWrap(true);
		mLog.setBackground(Color.WHITE);
		mLog.setBounds(50, 190, 1200, 570);
		mLog.setFont(new Font("Dialog", 0, 14));
		
		mLabel.setFont(new Font("Dialog", 1, 18));
		mLabel.setBounds(180, 40, 150, 40);
		mText.setFont(new Font("Dialog",1, 17));
		mText.setBounds(320, 40, 450, 40);
		tLabel.setFont(new Font("Dialog", 1, 18));
		tLabel.setBounds(800, 40, 150, 40);
		mTime.setFont(new Font("Dialog",1, 17));
		mTime.setBounds(940, 40, 120, 40);
		startBtn.setFont(new Font("Dialog", 1, 17));
		reportBtn.setFont(new Font("Dialog", 1, 17));
		evalBtn.setFont(new Font("Dialog", 1, 17));
		startBtn.setBounds(180, 120, 150, 40);
		reportBtn.setBounds(550, 120, 150, 40);
		evalBtn.setBounds(920, 120, 150, 40);
		
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String inputPackage = mText.getText();
					String inputTime = mTime.getText();
					if(inputPackage.equals("")) 
						mLog.append(placeOutput("应用包名不可为空！"));
					else if(inputTime.equals(""))
						mLog.append(placeOutput("监测时间不可为空！"));
					else {
						Integer round = Integer.parseInt(inputTime);
						if(round > 300) {
							mLog.append("分析时间过长，请重设分析时间(<300秒)！");
							return;
						}
						startBtn.setEnabled(false);
						reportBtn.setEnabled(false);
						evalBtn.setEnabled(false);
						mLog.append(placeOutput("分析中，请稍候..."));
						
						for(int i=0; i<round; i++) {
							mLog.append(placeOutput("分析进度： "+i+"/"+round+"  ..."));
							fUtil.generateFile(inputPackage);
							if(fUtil.getGenerateResult() != null)
								mLog.append(placeOutput(fUtil.getGenerateResult()));
							
						}
						
						
						fUtil.readCpu(inputPackage);
						if(fUtil.getReadCpuResult() != null)
							mLog.append(placeOutput(fUtil.getReadCpuResult()));
						mLog.append(placeOutput("分析完成,生成cpuInfo.txt成功！"));
						fUtil.readMem(inputPackage);
						if(fUtil.getReadMemResult() != null)
							mLog.append(placeOutput(fUtil.getReadMemResult()));
						mLog.append(placeOutput("分析完成,生成memInfo.txt成功！"));
					}
					startResult = true;
					startBtn.setEnabled(true);
					reportBtn.setEnabled(true);
					evalBtn.setEnabled(true);
				}catch(Exception ex) {
					mLog.append(placeOutput(ex.getStackTrace().toString()));
					//ex.printStackTrace();
				}
			}	
		});
		
		reportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(startResult == false) {
					mLog.append(placeOutput("请先等待分析结束."));
				}
				else {
					startBtn.setEnabled(false);
					reportBtn.setEnabled(false);
					evalBtn.setEnabled(false);
					fUtil.writeFile();
					if(fUtil.getWriteResult() != null)
						mLog.append(placeOutput(fUtil.getWriteResult()));
				}
				reportResult = true;
				mLog.append(placeOutput("报告已生成,生成cpuAnalysis.txt."));
				mLog.append(placeOutput("报告已生成,生成memAnalysis.txt."));
				startBtn.setEnabled(true);
				reportBtn.setEnabled(true);
				evalBtn.setEnabled(true);
			}	
		});
		
		evalBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(reportResult == false) 
					mLog.append(placeOutput("请先生成报告!"));
				else {
					List<String> str_app = fUtil.getCpuList();
					List<String> str_mem = fUtil.getMemList();
					List<Double> dou_app = cUtil.convertToDouble(str_app);
					List<Double> dou_mem = cUtil.convertToDouble(str_mem);
					
					mLog.append(placeOutput("CPU使用率最大值： "+Collections.max(str_app)+" %"));
					mLog.append(placeOutput("CPU使用率最小值： "+Collections.min(str_app)+" %"));
					mLog.append(placeOutput("CPU使用率平均值： "+cUtil.convertToAverage(dou_app)+" %"));
					
					mLog.append(placeOutput("物理内存使用最大值： "+Collections.max(str_mem)+" KB"));
					mLog.append(placeOutput("物理内存使用最小值： "+Collections.min(str_mem)+" KB"));
					mLog.append(placeOutput("物理内存使用平均值： "+cUtil.convertToAverage(dou_mem)+" KB"));
			
					mLog.append(placeOutput("性能评价完成！"));
				}
			}
		});
		
		mPanel.add(mLog);
		mPanel.add(mScroll);
		mPanel.add(mLabel);
		mPanel.add(tLabel);
		mPanel.add(mText);
		mPanel.add(mTime);
		mPanel.add(startBtn);
		mPanel.add(reportBtn);
		mPanel.add(evalBtn);
	}
	
	
}
