package com.androidcollider.vysotsky.listeners;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by pseverin on 19.01.15.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }


    private void selectItem(int position) {
        // update the main content by replacing fragments
        /*Fragment fragment = new CatFragment();
        Bundle args = new Bundle();
        args.putInt(CatFragment.ARG_CAT_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mCatTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);*/
    }


}