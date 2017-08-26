# SeatPickView
Gradle 引入
<pre>
compile 'me.ywx.SeatPickView:seatlibrary:1.0.7'
</pre>



 xml引入
 ```xml
 <com.example.ywx.mylibrary.SeatPickView
        app:max_seat="1"  //指定最大可选座位数量
        android:id="@+id/pick"
        android:layout_width="wrap_content"    //指定wap_content默认等于屏幕高度一半
        android:layout_height="wrap_content"/>  //指定wap_content默认等于屏幕宽度
 ```
        
        
 基本用法
  ```Java
    public class MainActivity extends AppCompatActivity {
    private SeatPickView pick;
    private List<SeatInfo>list=new ArrayList<>();   //座位数组
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pick=(SeatPickView)findViewById(R.id.pick); 
        
        for(int i=0;i<12;i++) {
            SeatInfo seatInfo = new SeatInfo();
            seatInfo.setRow(9);//设置行数(从0开始)
            seatInfo.setSeatstatue(0);  //当前座位状态
            seatInfo.setColums(i);//设置列数(从0开始)
            seatInfo.setChoose(false);//填false,如果该座位空闲将展示可选状态
            list.add(seatInfo);
        }
        pick.setSeatList(list);  //绑定数据源
    }
}
```


绑定监听器
```java
        pick.setOnSelectedMaxListener(new OnSelectedMaxListener() {
            @Override
            public void onSelectedMax(int selectedCount) {
            //当点击超过设置的最大座位在这里进行提醒或处理
                Toast.makeText(MainActivity.this,"最多只能选3个",Toast.LENGTH_SHORT).show();
            }
        });
        pick.setOnSelectListener(new OnSelectListener() {
            @Override
            public void onSelect(SeatInfo seatInfo) {
            //选择一个座位时会回调这里(座位空闲才回调)
                Toast.makeText(MainActivity.this,"选择了"+(seatInfo.getRow()+1)+","+(seatInfo.getColums()+1),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRemove(SeatInfo seatInfo) {
            //取消选择一个座位时会回调这里(座位空闲才回调)
                Toast.makeText(MainActivity.this,"取消了"+(seatInfo.getRow()+1)+","+(seatInfo.getColums()+1),Toast.LENGTH_SHORT).show();
            }
        });
        
        //获取选择的座位数组
        pick.getSelectedSeatList();
        
        //ps:获取座位行列位置时都是从0开始，如1行1列，获取到的是0行0列
```

SeatInfo类
```java

public class SeatInfo {
    private int sid;//座位id
    private int fid;//楼层id
    private int leftside;//座位位置左右
    private String seatnumber;//座位编号
    private int row;//行数
    private int colums;//列数
    private int statue;//座位开放状态
    private int seatstatue;//座位预约状态
    private boolean isChoose;//可预约状态下座位是否被选中
    }
```
        
        
     
