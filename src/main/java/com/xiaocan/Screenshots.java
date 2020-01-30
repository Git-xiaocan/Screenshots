package com.xiaocan;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.security.provider.SHA;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Screenshots extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Stage Pstage;//主舞台

    private double screenX_start;//开始X坐标
    private double screenY_start;//开始Y坐标
   private  double width;
    private double height;
    private HBox view;
   public static int screenWidth ;
   public static int screenHeight;
     Label label = new Label("1111111");
    Button button = new Button("完成");
    BufferedImage image = null;
    AnchorPane anchorPane =null;
    @Override
    public void start(Stage primaryStage) {
        Pstage = primaryStage;
        anchorPane = new AnchorPane();
        Scene scene = new Scene(anchorPane, 400, 100);

        Button bu = new Button("点击截图");

        bu.setStyle("-fx-fill-width: 50px;-fx-fill-height:30px; ");

        bu.setOnMouseClicked(e -> {
            show();
        });


        anchorPane.getChildren().add(bu);
        AnchorPane.setLeftAnchor(bu, 20.0);
        AnchorPane.setTopAnchor(bu, 20.0);


        primaryStage.setScene(scene);
        primaryStage.setTitle("屏幕截图");
        primaryStage.show();


    }

    /**
     * 点击截图按钮后 创建截图的窗口
     */
    public void show() {
        Pstage.setIconified(true);
        Stage stage = new Stage();
        StackPane stackPane = new StackPane();
        ImageView imageView = getAllScreenImage();
        stackPane.getChildren().add(imageView);
        AnchorPane anchorPane = new AnchorPane();
        StartScreenShots(anchorPane,stage);
        anchorPane.setStyle("-fx-background-color: #ffff1122");
        stackPane.getChildren().add(anchorPane);
        Scene scene = new Scene(stackPane, 1920, 1080);
        scene.setFill(Paint.valueOf("#ffffff00"));
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setFullScreenExitHint("");//窗口最大时的提示内容

        stage.setScene(scene);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                    Pstage.setIconified(false);
                }
            }
        });

        stage.show();
        stage.setFullScreen(true);

    }

    /**
     * 开始截图操作
     */
    public void StartScreenShots(AnchorPane anchorPane,Stage stage) {
        view = new HBox();
        view.setBackground(null);
        view.setBorder(new Border(new BorderStroke(Paint.valueOf("#CD3700"), BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
        anchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                view.setPrefWidth(0);
                view.setPrefHeight(0);
                screenX_start = event.getScreenX();
                screenY_start = event.getScreenY();

                ObservableList ob = anchorPane.getChildren();
                if (ob.size() != 0) {
                    //如果面板中已经有hbox  先删除
                   ob.clear();


                }
                anchorPane.getChildren().add(view);
                anchorPane.getChildren().add(label);
                AnchorPane.setLeftAnchor(label,screenX_start);
                AnchorPane.setTopAnchor(label,screenY_start-20.0);


                AnchorPane.setTopAnchor(view, screenY_start);
                AnchorPane.setLeftAnchor(view, screenX_start);

            }
        });
        anchorPane.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorPane.startFullDrag();
            }
        });

        anchorPane.setOnMouseDragOver(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double sceneX = event.getSceneX();
                double sceneY = event.getScreenY();
                width = sceneX - screenX_start;
                height = sceneY - screenY_start;
                label.setStyle("-fx-background-color: #000");
                label.setText(width+" x " + height);
                view.setPrefWidth(width);
                view.setPrefHeight(height);
                //开始截屏


            }
        });
        anchorPane.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                button = new Button("完成");
                anchorPane.getChildren().add(button);
                AnchorPane.setTopAnchor(button,event.getY()+10.0);
                AnchorPane.setLeftAnchor(button,event.getX()-button.getWidth());
                button.setOnMouseClicked(e->{
                    try {
                        image = (new Robot()).createScreenCapture(new Rectangle((int)screenX_start+2,(int)screenY_start+2,(int)width-4,(int)height-4));
                        stage.close();
                        Pstage.setIconified(true);
                       showImage();


                    } catch (AWTException ex) {
                        ex.printStackTrace();
                    }

                });
            }
        });
    }

    private void showImage() {
        Stage stage = new Stage();
       AnchorPane anchorPane = new AnchorPane();

        Scene scene = new Scene(anchorPane,500,520);
        WritableImage imagefx = SwingFXUtils.toFXImage(image,null);
        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putImage(imagefx);
        cb.setContent(content);
        ImageView imageView = new ImageView(imagefx);
        imageView.setFitWidth(500);
        imageView.setFitHeight(500);
        imageView.setPreserveRatio(true);
        anchorPane.getChildren().add(new ImageView(imagefx));
        Button bu = new Button("保存");
       bu.setPrefWidth(50);
       bu.setPrefHeight(20);
       bu.setOnMouseClicked(e->{
           //保存文件
           FileChooser chooser = new FileChooser();//创建一个文件对话框
           chooser.setTitle("保存图片");
           chooser.setInitialDirectory(new File("C://"));//设置文件对话框初始目录
           //创建文件过滤器
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("图像文件(*.jpg)","*.jpg");

            chooser.getExtensionFilters().add(filter);
           File file = chooser.showSaveDialog(stage);
           try {
               ImageIO.write(image,"jpeg",file);
               stage.close();
               Pstage.setIconified(false);
           } catch (IOException ex) {
               ex.printStackTrace();
           }
       });
        HBox hBox = new HBox();
        hBox.setPrefWidth(500);
        hBox.getChildren().add(bu);
        hBox.setAlignment(Pos.CENTER);

        anchorPane.getChildren().add(hBox);
        AnchorPane.setBottomAnchor(hBox,0.0);






        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * 获取整个屏幕图片
     * @return
     */
    public static ImageView getAllScreenImage() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         screenWidth = screenSize.width;
         screenHeight = screenSize.height;

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        BufferedImage bi = robot.createScreenCapture(new Rectangle(screenWidth, screenHeight));
        Image image = SwingFXUtils.toFXImage(bi, null);
        ImageView imageView = new ImageView(image);

        return imageView;
    }
}