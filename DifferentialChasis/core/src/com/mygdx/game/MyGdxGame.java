package com.mygdx.game;

import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.JFrame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.awt.*;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    float spriteX, spriteY;
    float x, y;
    double L = 20;
    double speed;
    float phi;
    float omega;
    float rotation;
	float height = 100;
	float width = 100;
    File f1, f2, f3, f4;
    ShapeRenderer shapeRenderer;
    Texture img2;
    float dt;
    double t;
    ArrayList<Double> speedData = new ArrayList<>();
    ArrayList<Double> timeData = new ArrayList<>();
    ArrayList<Double> xData = new ArrayList<>(); 
    ArrayList<Double> yData = new ArrayList<>(); 

    ArrayList<Double> xDataRight = new ArrayList<>(); 
    ArrayList<Double> yDataRight = new ArrayList<>(); 

    ArrayList<Double> xDataLeft = new ArrayList<>(); 
    ArrayList<Double> yDataLeft = new ArrayList<>(); 

    @Override
    public void create () {
        batch = new SpriteBatch();
        img = new Texture("2237048.png"); 
    
        shapeRenderer = new ShapeRenderer();
        spriteX = 100;
        spriteY = 100;
        dt = 0.1f;
        phi = 0;
  
        x = spriteX;
        y = spriteY;

        speed = 0;
        rotation = 0;
        t = 0;
        f1 = new File("pointsSpeedTime.csv");
        f1.delete();

        f2 = new File("pointsPolohovanie.csv");
        f2.delete();

        f3 = new File("pointsRightWheel.csv");
        f3.delete();

        f4 = new File("pointsLeftWheel.csv");
        f4.delete();
        
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (Gdx.input.isKeyJustPressed(Keys.W)) {
            speed += 0.1; 
        } else if (Gdx.input.isKeyJustPressed(Keys.S)) {
            speed -= 0.1; 
        }
        if (Gdx.input.isKeyJustPressed(Keys.A)) {
            omega += 0.5;
        }
        if (Gdx.input.isKeyJustPressed(Keys.D)) {
            omega -= 0.5; 
        }
        if(Gdx.input.isKeyJustPressed(Keys.R)){
            omega = 0;
        }
        if(Gdx.input.isKeyJustPressed(Keys.Q)){
            speed = 0;
        }
        if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
            Gdx.app.exit();
            plottheGraph(readAllData("pointsSpeedTime.csv", "1"), readAllData("pointsPolohovanie.csv", "2"), readAllData("pointsRightwheel.csv", "3"), readAllData("pointsLeftwheel.csv", "4"));
            
        }
        
        phi += omega*dt;

        double radianAngle = (float) Math.toRadians(phi);
        double deltaX = speed * (float) Math.cos(radianAngle);
        double deltaY = speed * (float) Math.sin(radianAngle);
    
        
        if (x + deltaX >= 0 && x + deltaX <= Gdx.graphics.getWidth() - width) {
            x += deltaX;
        }
        if (y + deltaY >= 0 && y + deltaY <= Gdx.graphics.getHeight() - height) {
            y += deltaY;
        }

        t += Gdx.graphics.getDeltaTime();
        timeData.add(t);
        speedData.add(speed);
        writeAllData(t, speed , "pointsSpeedTime.csv");
        writeAllData(x, y , "pointsPolohovanie.csv");
        xData.add((double)x);
        yData.add((double)y);

        double xRight = x - (L / 2) * Math.cos(radianAngle + (Math.PI/2));
        double yRight = y - (L / 2) * Math.sin(radianAngle + (Math.PI/2));

        double xLeft = x + (L / 2) * Math.cos(radianAngle + (Math.PI/2));
        double yLeft = y + (L / 2) * Math.sin(radianAngle + (Math.PI/2));

        xDataRight.add(xRight);
        yDataRight.add(yRight);

        xDataLeft.add(xLeft);
        yDataLeft.add(yLeft);
        writeAllData(xRight, yRight , "pointsRightWheel.csv");
        writeAllData(xLeft, yLeft , "pointsLeftWheel.csv");
    
        batch.begin();
        
        batch.draw(img, x, y, width / 2, height / 2, width, height, 1, 1, phi, 0, 0, img.getWidth(), img.getHeight(), false, false);
        
        batch.end();
    }

    public void writeAllData(double t, double speed, String namefile){

        
        try (CSVWriter writer = new CSVWriter(new FileWriter(namefile, true))) {
        
            String[] s = {Double.toString(t), Double.toString(speed)};
            writer.writeNext(s);

        } catch (IOException e) {
            e.printStackTrace();
        } 

        
    }

    public XYSeries readAllData(String namefile, String key){

        XYSeries series = new XYSeries(key);

        
        try (CSVReader reader = new CSVReader(new FileReader(namefile))) {
            String[] line;
            
            while ((line = reader.readNext()) != null) {

                double speed = Double.parseDouble(line[0]);
                double time = Double.parseDouble(line[1]);
                
                series.add(speed, time);
                
                
                
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return series;
    }

    public void plottheGraph(XYSeries series, XYSeries series1, XYSeries series2, XYSeries series3){

        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(series);
        XYSeriesCollection dataset2 = new XYSeriesCollection();
        dataset2.addSeries(series1);
        dataset2.addSeries(series2);
        dataset2.addSeries(series3);
        JFreeChart chart1, chart2;


        chart1 = ChartFactory.createXYLineChart(
        "Speed per Second",          
        "Time [s]",         
        "Speed [m/s]",         
        dataset1);

        chart2 = ChartFactory.createScatterPlot(
        "Your Trajectory",          
        "",         
        "",         
        dataset2, org.jfree.chart.plot.PlotOrientation.VERTICAL, false, false, false);

        XYPlot plot1 = (XYPlot) chart1.getPlot();;

        plot1.getRenderer().setSeriesPaint(0, Color.BLACK);

        
        plot1.setDomainGridlinesVisible(false);
        plot1.setRangeGridlinesVisible(false);
        plot1.setBackgroundPaint(Color.WHITE);

        

        XYPlot plot = (XYPlot) chart2.getPlot();
        plot.getRenderer().setSeriesPaint(0, Color.BLACK);
        plot.getRenderer().setSeriesPaint(1, Color.GREEN);
        plot.getRenderer().setSeriesPaint(2, Color.RED);

        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setBackgroundPaint(Color.WHITE);

        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        xAxis.setTickLabelsVisible(false);
        yAxis.setTickLabelsVisible(false);

        
        ChartPanel chartPanel1 = new ChartPanel(chart1);

        
        JFrame frame1 = new JFrame("JFreeChart Example");
        frame1.setContentPane(chartPanel1);
        frame1.setSize(1200, 800);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setVisible(true);

        ChartPanel chartPanel2 = new ChartPanel(chart2);

        
        JFrame frame2 = new JFrame("JFreeChart Example");
        frame2.setContentPane(chartPanel2);
        frame2.setSize(1200, 800);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setVisible(true);

    }

    

    @Override
    public void dispose () {
        batch.dispose();
        img.dispose();
    }
}