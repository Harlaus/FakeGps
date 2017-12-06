package name.caiyao.fakegps.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import name.caiyao.fakegps.R;
import name.caiyao.fakegps.data.DbHelper;
import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class HelpFragment extends Fragment {
    Toolbar toolbar;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
         toolbar=new Toolbar(getActivity());

        Log.i("520it", "HelpFragment" + "**********    onCreate  ****************");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i("520it", "HelpFragment" + "************  coCreateOptionsMenu **************");
        menu.clear();
        inflater.inflate(R.menu.menu_parent_fragment, menu);

        super.onCreateOptionsMenu(menu,inflater);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.help, container, false);
    }
}
