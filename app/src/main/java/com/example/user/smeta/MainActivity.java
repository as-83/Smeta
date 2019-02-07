package com.example.user.smeta;

        import android.database.Cursor;
        import android.database.SQLException;
        import android.database.sqlite.SQLiteDatabase;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.Spinner;
        import android.widget.Toast;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String ATTRIBUTE_NAME_TEXT = "text";
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    // названия(групп)
    String[] groups = new String[] {"Штукатурка",
            "Электромонтаж",
            "Шпаклевка",
            "Плиточные работы",
            "Обои",
            "Покрасочные работы",
            "Отделка пола",
            "Разное",
            "Грунтовки и т.п.",
            "Сантехника",
            "Демонтажные работы",
            "ГКЛ"
    };

    String[] subGroups = new String[]{
            "Пол",
            "Стены",
            "Откосы",
            "Колонны",
            "Потолок",
            "Водоснабжение",
            "Канализация",
            "Отопление",
            "Демонтаж электромонтажных изделий",
            "Прокладка кабеля",
            "Монтаж розеток и выключателей",
            "Монтаж автоматов защиты и электрощитков",
            "Установка и подключение электрооборудования",
            "Подготовка",
            "Отделка",
            "Двери",
            "ПВХ и МДФ",
            "Кладка",
            "Разное",
            "Ванные, Джакузи, Душевые",
            "Унитазы, Раковины, Мойки, Смесители",
            "Фильтры",
            "Стиральные и посудомоечные машины"
    };
    String[] currentSubGroup;

    int subGroupsIndex [][] = {
            {2, 3, 4, 5},
            {9, 10, 11, 12, 13},
            {2, 3, 4, 5},
            {1, 2, 3, 4},
            {2, 3, 4, 5},
            {3, 4, 5},
            {14, 15},
            {16, 17, 18, 19},
            {2, 3, 4, 5, 19},
            {6, 7, 8, 20, 21, 22, 23},
            {1, 2, 3, 4, 5},
            {2, 3, 4, 5}};

    Spinner groupSpiner, subgroupSpinner;
    ListView listView;
    SimpleAdapter adapter3;
    ArrayAdapter<String> spinnerAdapter1, spinnerAdapter2 ;
    ArrayList<Map<String, Object>> data;
    Map<String, Object> map;
    Cursor cursor;




    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupSpiner = (Spinner)findViewById(R.id.spinnerGroup);
        subgroupSpinner = (Spinner)findViewById(R.id.spinnerSub) ;
        listView = (ListView)findViewById(R.id.listView);

        mDBHelper = new DatabaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        spinnerAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, groups);
        groupSpiner.setAdapter(spinnerAdapter1);

        groupSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currentSubGroup = createCurrentSubGroup(position);
                subgroupSpinner.setEnabled(true);

                spinnerAdapter2 = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_item, currentSubGroup);
                subgroupSpinner.setAdapter(spinnerAdapter2);
                subgroupSpinner.setPrompt("MySpinner");

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        subgroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                data.clear();
                int sub = subGroupsIndex[ groupSpiner.getSelectedItemPosition() ][position] ;
                cursor = mDb.rawQuery("SELECT _id, work_name, price FROM smeta where work_class = " + (groupSpiner.getSelectedItemPosition() +1) + " and work_subclass = " + sub + ";", null);
                cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            map = new HashMap<String, Object>();
                            // map.put("_id", cursor.getInt(0));
                            map.put("_id", cursor.getInt(0));
                            map.put("work_name", cursor.getString(1));
                            map.put("price", cursor.getDouble(2));
                            // добавляем его в коллекцию
                            data.add(map);

                            // уведомляем, что данные изменились
                            cursor.moveToNext();
                        }

                        cursor.close();
                        adapter3.notifyDataSetChanged();

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // массив имен атрибутов, из которых будут читаться данные
        final String[] from = { "work_name", "price"};
        // массив ID View-компонентов, в которые будут вставлять данные
        final int[] to = {R.id.tvText, R.id.textView2};
        cursor = mDb.rawQuery("SELECT _id, work_name, price FROM smeta where work_class = " + 1 + " and work_subclass = " + 2 + ";", null);
        startManagingCursor(cursor);

        // упаковываем данные в понятную для адаптера структуру
        data = new ArrayList<Map<String, Object>>();
        adapter3 = new SimpleAdapter(this, data, R.layout.item, from, to);
        listView.setAdapter(adapter3);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                String str = data.get(position).get("work_name").toString() + ", id = "+data.get(position).get("_id").toString() ;
                                                Toast.makeText(getBaseContext(), "Вы выбрали: " + str, Toast.LENGTH_SHORT).show();
                                            }
                                        }

        );



    }

    private String[] createCurrentSubGroup(int ind) {
        String[] str =  new String[subGroupsIndex[ind].length];
        for (int i = 0 ; i< subGroupsIndex[ind].length; i++) {
                str[i] = subGroups[subGroupsIndex[ind][i] - 1];
            }
        return str;
    }

}

