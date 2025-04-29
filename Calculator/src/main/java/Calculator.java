package main.java;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static java.awt.Font.*;

public class Calculator {
     private JFrame jf;
     private JTextField jtf;
     private String expression="";//存输入的计算表达式
     private double result=0;
     private double memory=0;
     private boolean isRadianMode=true;   //// 角度/弧度模式切换，true为弧度模式
     // 使用单例模式缓存HTML帮助文档
     private JFrame htmlFrame = null;
     private JEditorPane editorPane = null;

    public Calculator(){
        jf=new JFrame();
        jf.setTitle("科学计算器");
        jf.setBounds(400,300,500,600);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLayout(new BorderLayout());
        jf.setVisible(true);

        //菜单栏、
        JMenuBar menuBar =new JMenuBar();

        JMenu checkMenu=new JMenu("查看");
        JMenuItem tableItem=new JMenuItem("功能表");

        JMenu editMenu=new JMenu("编辑");
        JMenuItem copyItem =new JMenuItem("复制");
        JMenuItem pasteItem=new JMenuItem("粘贴");

        JMenu helpMenu=new JMenu("帮助");
        JMenuItem helpItem=new JMenuItem("???");
      
      tableItem.addActionListener(e->{
          try{
              if(htmlFrame == null){
                  String relativePath = "src/main/resource/table.html";
                  File htmlFile =new File(relativePath);
                  if(htmlFile.exists()){
                      htmlFrame = new JFrame("功能表");
                      htmlFrame.setSize(800,600);
                      htmlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                      editorPane = new JEditorPane();
                      editorPane.setEditable(false);
                      editorPane.setContentType("text/html");
                      editorPane.setPage(htmlFile.toURI().toURL());

                      JScrollPane scrollPane = new JScrollPane(editorPane);
                      htmlFrame.add(scrollPane,BorderLayout.CENTER);
                  }else {
                      JOptionPane.showMessageDialog(jf,"HTML 文件不存在！\n请检查路径："+htmlFile.getAbsolutePath(),"错误",JOptionPane.ERROR_MESSAGE);
                      return;
                  }
              }
              htmlFrame.setVisible(true);
          }catch(Exception ex){
              ex.printStackTrace();
              JOptionPane.showMessageDialog(jf,"无法加载HTML文件","错误",JOptionPane.ERROR_MESSAGE);
          }
      });

        //复制粘贴，AI写的，不懂

        copyItem.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(jtf.getText());
            clipboard.setContents(selection, null);
        });

        pasteItem.addActionListener(e -> {
            try {
                String str=jtf.getText();
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String data = (String) clipboard.getData(DataFlavor.stringFlavor);
                jtf.setText(str+data);
                expression = data;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jf, "无法粘贴", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        menuBar.add(editMenu);

        checkMenu.add(tableItem);
        menuBar.add(checkMenu);

        helpMenu.add(helpItem);
        menuBar.add(helpMenu);

        jf.setJMenuBar(menuBar);



        jtf=new JTextField();
        jtf.setFont(new Font("Arial", Font.BOLD,24));
        jtf.setHorizontalAlignment(JTextField.RIGHT);
        jtf.setPreferredSize(new Dimension(400, 80));//首选大小
       jtf.setVisible(true);

       //北方面板存文本框
       JPanel northPanel=new JPanel();
       northPanel.setLayout(new BorderLayout());
       northPanel.add(jtf,BorderLayout.SOUTH);
       jf.add(northPanel,BorderLayout.NORTH);

       //按钮面板
       JPanel jp=new JPanel();
       jp.setLayout(new GridLayout(8,5,5,5));  //8行5列，间距为5
        jp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        jp.setVisible(true);
        jf.add(jp,BorderLayout.CENTER);
        //BorderFactory 是一个工具类，提供了多种创建边框的方法。
        //createEmptyBorder(int top, int left, int bottom, int right) 方法用于创建一个空白边框，即不绘制任何可见边框，但会为组件添加内边距（padding）。
      String []buttons={

              "Rad", "MC", "MR", "MS", "M+",
              "deg", "sin", "cos", "tan", "M-",
              "x²", "√", "log", "ln", "e^x",
              "<--", "CE", "C", "%", "(",
              "7", "8", "9", "/", ")",
              "4", "5", "6", "*", "1/x",
              "1", "2", "3", "-", "π",
              "0", ".", "=", "+", "±"
      };

      for(String label:buttons){
          JButton jb=new JButton(label);
          jb.addActionListener(new MyButtonListener());
          jb.setFont(new Font("Arial", Font.PLAIN, 16));
          if ("0123456789.".contains(label)) {
              jb.setBackground(new Color(232, 255, 232));
          } else if ("=".contains(label)) {
              jb.setBackground(new Color(230, 230, 250));
          } else if ("+-/*<--CE%".contains(label)) {
              jb.setBackground(new Color(255, 228, 225));
          } else {
              jb.setBackground(new Color(220, 220, 220));
          }

          jp.add(jb);
      }



    }

 public class MyButtonListener implements ActionListener {
        @Override
     public void actionPerformed(ActionEvent e){
            String command = e.getActionCommand();//获取按钮上的字符串
            try{
                switch(command){
                    case "=":
                        result = evaluateExpression(expression);
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "C":
                    case "CE":
                        jtf.setText("");
                        expression = "";
                        break;
                    case "<--":
                        if (!expression.isEmpty()) {
                            expression = expression.substring(0, expression.length() - 1);
                            jtf.setText(expression);
                        }
                        break;
                    case "MC":
                        memory = 0;
                        break;
                    case "MR":
                        expression = String.valueOf(memory);
                        jtf.setText(expression);
                        break;
                    case "MS":
                        memory = Double.parseDouble(jtf.getText());
                        break;
                    case "M+":
                        memory += Double.parseDouble(jtf.getText());
                        break;
                    case "M-":
                        memory -= Double.parseDouble(jtf.getText());
                        break;
                    case "±":
                        if (!expression.isEmpty()) {
                            if (expression.startsWith("-")) {
                                expression = expression.substring(1);
                            } else {
                                expression = "-" + expression;
                            }
                            jtf.setText(expression);
                        }
                        break;
                    case "π":
                        expression += String.valueOf(Math.PI);
                        jtf.setText(expression);
                        break;
                    case "e^x":
                        result = Math.exp(Double.parseDouble(jtf.getText()));
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "sin":
                        double angle = Double.parseDouble(jtf.getText());
                        if (!isRadianMode) {
                            angle = Math.toRadians(angle);
                        }
                        result = Math.sin(angle);
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "cos":
                        angle = Double.parseDouble(jtf.getText());
                        if (!isRadianMode) {
                            angle = Math.toRadians(angle);
                        }
                        result = Math.cos(angle);
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "tan":
                        angle = Double.parseDouble(jtf.getText());
                        if (!isRadianMode) {
                            angle = Math.toRadians(angle);
                        }
                        result = Math.tan(angle);
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "log":
                        result = Math.log10(Double.parseDouble(jtf.getText()));
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "ln":
                        result = Math.log(Double.parseDouble(jtf.getText()));
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "x²":
                        result = Math.pow(Double.parseDouble(jtf.getText()), 2);
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "√":
                        result = Math.sqrt(Double.parseDouble(jtf.getText()));
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "1/x":
                        result = 1 / Double.parseDouble(jtf.getText());
                        jtf.setText(String.valueOf(result));
                        expression = String.valueOf(result);
                        break;
                    case "Rad":
                    case "deg":
                        isRadianMode = !isRadianMode;
                        break;
                    default:
                        expression += command;
                        jtf.setText(expression);
                }
            }catch (Exception ex) {
                JOptionPane.showMessageDialog(jf, "计算错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
 }


 private double evaluateExpression(String expression) throws Exception{
        return new ExpressionEvaluator().evaluate(expression);
 }

 // 优化后的非递归解析器，带缓存功能
 private static class ExpressionEvaluator{
        private static final Map<String, Double> cache = new ConcurrentHashMap<>();
        
        public double evaluate(String expression) throws Exception {
            // 检查缓存
            if(cache.containsKey(expression)) {
                return cache.get(expression);
            }
            
            // 去除空格
            expression = expression.replaceAll("\\s+", "");
            
            Stack<Double> numbers = new Stack<>();
            Stack<Character> operators = new Stack<>();
            int i = 0;
            int n = expression.length();
            
            while (i < n) {
                char c = expression.charAt(i);
                
                if (Character.isDigit(c) || c == '.') {
                    // 解析数字
                    StringBuilder numStr = new StringBuilder();
                    while (i < n && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                        numStr.append(expression.charAt(i++));
                    }
                    numbers.push(Double.parseDouble(numStr.toString()));
                } else if (c == '(') {
                    operators.push(c);
                    i++;
                } else if (c == ')') {
                    while (operators.peek() != '(') {
                        numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                    }
                    operators.pop(); // 弹出'('
                    i++;
                } else if (isOperator(c)) {
                    while (!operators.empty() && precedence(operators.peek()) >= precedence(c)) {
                        numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                    }
                    operators.push(c);
                    i++;
                } else {
                    throw new RuntimeException("无效字符: " + c);
                }
            }
            
            while (!operators.empty()) {
                numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
            }
            
            double result = numbers.pop();
            // 缓存结果
            cache.put(expression, result);
            return result;
        }
        
        private boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
        }
        
        private int precedence(char op) {
            switch (op) {
                case '+':
                case '-':
                    return 1;
                case '*':
                case '/':
                case '%':
                    return 2;
            }
            return 0;
        }
        
        private double applyOp(char op, double b, double a) {
            switch (op) {
                case '+': return a + b;
                case '-': return a - b;
                case '*': return a * b;
                case '/': 
                    if (b == 0) throw new RuntimeException("除数不能为零");
                    return a / b;
                case '%': 
                    if (b == 0) throw new RuntimeException("除数不能为零");
                    return a % b;
            }
            throw new RuntimeException("无效运算符: " + op);
        }
 }


    public static void main(String[] args) {
        new Calculator();
    }


}
