package com.blueodin.wifigraphs;

import java.util.ArrayList;

import com.blueodin.graphs.charts.PointOnChart;

import com.blueodin.graphs.linecharts.LineChartAttributes;
import com.blueodin.graphs.linecharts.LineChartView;
import com.blueodin.graphs.linecharts.PathAttributes;
import com.blueodin.graphs.linecharts.PathOnChart;
import android.app.Activity;
import android.os.Bundle;

public class GraphDemoActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ArrayList<PointOnChart> points1 = new ArrayList<PointOnChart>();
        for(int i=30; i>=-30; i--){

            float X = i;
            float Y = (4*X*X)-1500;

            points1.add(new PointOnChart(X,-Y));
            //System.out.println("( X : "+ X + ", Y : "+ Y+" )");

        }

        ArrayList<PointOnChart> points2 = new ArrayList<PointOnChart>();
        for(int i=30; i>=-30; i--){

            float X = i;
            float Y = (4*X*X)- 0;

            points2.add(new PointOnChart(X,-Y));
            //System.out.println("( X : "+ X + ", Y : "+ Y+" )");

        }

        ArrayList<PointOnChart> points3 = new ArrayList<PointOnChart>();
        for(int i=30; i>=-30; i--){

            float X = i;
            float Y = (4*X*X) + 1500;

            points3.add(new PointOnChart(X,-Y));
            //System.out.println("( X : "+ X + ", Y : "+ Y+" )");

        }

        ArrayList<PointOnChart> points4 = new ArrayList<PointOnChart>();
        for(int i=30; i>=-30; i--){

            float X = i;
            float Y = (4*X*X) - 1300;

            points4.add(new PointOnChart(X,-Y));
            //System.out.println("( X : "+ X + ", Y : "+ Y+" )");

        }

        ArrayList<PointOnChart> points5 = new ArrayList<PointOnChart>();
        for(int i=30; i>=-30; i--){

            float X = i;
            float Y = (4*X*X) - 200;

            points5.add(new PointOnChart(X,-Y));
            //System.out.println("( X : "+ X + ", Y : "+ Y+" )");

        }

        ArrayList<PointOnChart> points6 = new ArrayList<PointOnChart>();
        for(int i=30; i>=-30; i--){

            float X = i;
            float Y = (4*X*X)+ 1300;

            points6.add(new PointOnChart(X,-Y));
            //System.out.println("( X : "+ X + ", Y : "+ Y+" )");

        }

        PathAttributes pathAttributes1 = new PathAttributes();
        pathAttributes1.setPointColor("#00AAAAAA");
        pathAttributes1.setPathColor("#FFAF00");
        PathOnChart path1 = new PathOnChart(points1, pathAttributes1);

        PathAttributes pathAttributes2 = new PathAttributes();
        pathAttributes2.setPointColor("#00AAAAAA");
        pathAttributes2.setPathColor("#FFFFFF");
        PathOnChart path2 = new PathOnChart(points2, pathAttributes2);

        PathAttributes pathAttributes3 = new PathAttributes();
        pathAttributes3.setPointColor("#00AAAAAA");
        pathAttributes3.setPathColor("#008000");
        PathOnChart path3 = new PathOnChart(points3, pathAttributes3);

        PathAttributes pathAttributes4 = new PathAttributes();
        pathAttributes4.setPointColor("#00AAAAAA");
        pathAttributes4.setPathColor("#FFAF00");
        PathOnChart path4 = new PathOnChart(points4, pathAttributes4);

        PathAttributes pathAttributes5 = new PathAttributes();
        pathAttributes5.setPointColor("#00AAAAAA");
        pathAttributes5.setPathColor("#FFFFFF");
        PathOnChart path5 = new PathOnChart(points5, pathAttributes2);

        PathAttributes pathAttributes6 = new PathAttributes();
        pathAttributes6.setPointColor("#00AAAAAA");
        pathAttributes6.setPathColor("#008000");
        PathOnChart path6 = new PathOnChart(points6, pathAttributes3);

        ArrayList<PathOnChart> paths = new ArrayList<PathOnChart>();
        paths.add(path1);
        paths.add(path2);
        paths.add(path3);
        paths.add(path4);
        paths.add(path5);
        paths.add(path6);

        LineChartAttributes lineChartAttributes = new LineChartAttributes();
        lineChartAttributes.setBackgroundColor("#c3c3c3");
        //lineChartAttributes.setGridVisible(false);
        setContentView(new LineChartView(this, paths, lineChartAttributes));

    }
}