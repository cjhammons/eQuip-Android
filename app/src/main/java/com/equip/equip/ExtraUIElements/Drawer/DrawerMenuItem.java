package com.equip.equip.ExtraUIElements.Drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.equip.equip.R;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.io.BufferedReader;

/**
 * Created by curtis on 7/4/17.
 *
 * Heavily based on the example given here: https://medium.com/@janishar.ali/navigation-drawer-android-example-8dfe38c66f59
 */

@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    public static final int DRAWER_MENU_ITEM_SEARCH = 1;
    public static final int DRAWER_MENU_ITEM_MY_RENTALS = 2;
    public static final int DRAWER_MENU_ITEM_MY_PROFILE = 3;
    public static final int DRAWER_MENU_ITEM_MESSAGES = 4;
    public static final int DRAWER_MENU_ITEM_SETTINGS = 5;
    public static final int DRAWER_MENU_ITEM_LOGOUT = 6;
    public static final int DRAWER_MENU_ITEM_MY_EQUIPMENT = 7;
    public static final int DRAWER_MENU_ITEM_DASHBOARD = 8;
    public static final int DRAWER_MENU_ITEM_ACCOUNT = 9;


    private int mMenuPosition;
    private Context mContext;
    private DrawerCallBack mCallBack;

    @View(R.id.itemNameTxt)
    private TextView itemNameTxt;

    @View(R.id.itemIcon)
    private ImageView itemIcon;

    private Drawable iconDrawable;

    public DrawerMenuItem(Context context, int menuPosition) {
        mContext = context;
        mMenuPosition = menuPosition;
    }

    public void setDrawable(Drawable drawable){
        this.iconDrawable = drawable;
    }

    @Resolve
    private void onResolved() {
        if (iconDrawable != null)
            itemIcon.setImageDrawable(iconDrawable);

        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_SEARCH:
                itemNameTxt.setText(R.string.drawer_search);
                break;
            case DRAWER_MENU_ITEM_MY_RENTALS:
                itemNameTxt.setText(R.string.drawer_my_rentals);
                break;
            case DRAWER_MENU_ITEM_MY_PROFILE:
                itemNameTxt.setText(R.string.drawer_my_profile);
                break;
            case DRAWER_MENU_ITEM_MESSAGES:
                itemNameTxt.setText(R.string.drawer_messages);
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                itemNameTxt.setText(R.string.drawer_settings);
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                itemNameTxt.setText(R.string.drawer_logout);
                break;
            case DRAWER_MENU_ITEM_MY_EQUIPMENT:
                itemNameTxt.setText(R.string.my_equipment);
                break;
            case DRAWER_MENU_ITEM_DASHBOARD:
                itemNameTxt.setText(R.string.drawer_dashboard);
                break;
            case DRAWER_MENU_ITEM_ACCOUNT:
                itemNameTxt.setText("Account");
                break;
        }
    }

    @Click(R.id.mainView)
    private void onMenuItemClick(){
        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_SEARCH:
                mCallBack.onItemSearchSelected();
                break;
            case DRAWER_MENU_ITEM_MY_RENTALS:
                mCallBack.onMyRentalsSelected();
                break;
            case DRAWER_MENU_ITEM_MY_PROFILE:
                mCallBack.onMyProfileSelected();
                break;
            case DRAWER_MENU_ITEM_MESSAGES:
                mCallBack.onMessagesSelected();
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                mCallBack.onSettingsSelected();
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                mCallBack.onLogoutSelected();
                break;
            case DRAWER_MENU_ITEM_MY_EQUIPMENT:
                mCallBack.onMyEquipmentSelected();
                break;
            case DRAWER_MENU_ITEM_DASHBOARD:
                mCallBack.onDashboardSelected();
                break;
            case DRAWER_MENU_ITEM_ACCOUNT:
                mCallBack.onAccountSelected();
                break;
        }
    }

    public void setDrawerCallBack(DrawerCallBack callBack) {
        mCallBack = callBack;
    }

    public interface DrawerCallBack{
        void onItemSearchSelected();
        void onMyRentalsSelected();
        void onMyProfileSelected();
        void onMessagesSelected();
        void onSettingsSelected();
        void onLogoutSelected();
        void onMyEquipmentSelected();
        void onDashboardSelected();
        void onAccountSelected();
    }
}
