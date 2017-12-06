package name.caiyao.fakegps.ui.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import name.caiyao.fakegps.R;
import name.caiyao.fakegps.dao.TempDao;

public class CollectionFragment extends Fragment {

    private FragmentActivity mfragment;
    private Toolbar toolbar;
    private ListView listview;
    private TempDao tempDao;
    private ArrayAdapter<String> adapter;
    private List<String> mList = new ArrayList<String>();
    private Button save;
    private Button clearall;
    private CalbackValue mCalbackValue;

    private void initView(View view) {

        listview = (ListView) view.findViewById(R.id.listview);
        save = (Button) view.findViewById(R.id.save);
        clearall = (Button) view.findViewById(R.id.clearall);

    }

    private List<String> initData() {
        tempDao = new TempDao(mfragment);
        mList = tempDao.selectAllData();
        return mList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);
        mfragment = getActivity();
        initView(view);
        toolbar = new Toolbar(mfragment);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, initData());
        listview.setAdapter(adapter);

        save.setText("模拟位置开始("+mList.size()+")");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {


                new AlertDialog.Builder(mfragment).setTitle("提示").setMessage("确定要删除位置？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tempDao = new TempDao(mfragment);
                        tempDao.deleteOnClick(position);
                        mList.remove(position);
                        adapter.notifyDataSetChanged();

                        save.setText("模拟位置开始("+mList.size()+")");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mList.size() == 0) {
                    Toast.makeText(getActivity(), "你没有选点", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mList.size() >7) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle("模拟位置开始");
                    builder.setMessage("位置会随着时间早上8点开始");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mCalbackValue.sendMessageValue(true);

                            Toast.makeText(getActivity(), "模拟位置开始,清除微信后台重进", Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
                } else {
                    Toast.makeText(getActivity(), "选择8个地点以上", Toast.LENGTH_SHORT).show();
                }
            }
        });


        clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearall();
            }
        });

    }

    public void clearall() {
        new AlertDialog.Builder(mfragment).setTitle("提示").setMessage("确定要删除所有位置吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tempDao = new TempDao(mfragment);
                tempDao.deleteTable();
                mList.clear();
                adapter.notifyDataSetChanged();

                save.setText("模拟位置开始("+mList.size()+")");
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frament_content, container, false);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_parent_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start:

                Toast.makeText(getActivity(), "FragmentMenuItem1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_clear:

                clearall();
                break;
        }

        return true;
    }


    /**
     * fragment与activity产生关联是  回调这个方法
     */

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

       mCalbackValue =(CalbackValue)getActivity();
    }


    /**
     * 定义一个回调接口
     */

    public interface CalbackValue{

        public  void sendMessageValue(boolean booleanValue);
    }

}