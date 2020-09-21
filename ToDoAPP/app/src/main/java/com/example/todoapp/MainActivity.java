package com.example.todoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import de.mrapp.android.tabswitcher.AbstractState;
import de.mrapp.android.tabswitcher.AddTabButtonListener;
import de.mrapp.android.tabswitcher.Animation;
import de.mrapp.android.tabswitcher.Layout;
import de.mrapp.android.tabswitcher.PeekAnimation;
import de.mrapp.android.tabswitcher.PullDownGesture;
import de.mrapp.android.tabswitcher.RevealAnimation;
import de.mrapp.android.tabswitcher.StatefulTabSwitcherDecorator;
import de.mrapp.android.tabswitcher.SwipeGesture;
import de.mrapp.android.tabswitcher.Tab;
import de.mrapp.android.tabswitcher.TabPreviewListener;
import de.mrapp.android.tabswitcher.TabSwitcher;
import de.mrapp.android.tabswitcher.TabSwitcherListener;
import de.mrapp.android.util.ThemeUtil;
import de.mrapp.android.util.multithreading.AbstractDataBinder;
import de.mrapp.util.Condition;

import static de.mrapp.android.util.DisplayUtil.getDisplayWidth;

public class MainActivity extends AppCompatActivity implements TabSwitcherListener {

    private class State extends AbstractState implements AbstractDataBinder.Listener<ArrayAdapter<String>, Tab, ListView, Void>, TabPreviewListener {

        private ArrayAdapter<String> adapter;

        State(@NonNull final Tab tab) {
            super(tab);
        }

        public void loadItems(@NonNull final ListView listView) {
            Condition.INSTANCE.ensureNotNull(listView, "The list view may not be null");

            if (adapter == null) {
                dataBinder.addListener(this);
                dataBinder.load(getTab(), listView);
            } else if (listView.getAdapter() != adapter) {
                listView.setAdapter(adapter);
            }
        }

        @Override
        public boolean onLoadData(
                @NonNull final AbstractDataBinder<ArrayAdapter<String>, Tab, ListView, Void> dataBinder,
                @NonNull final Tab key, @NonNull final Void... params) {
            return true;
        }

        @Override
        public void onCanceled(
                @NonNull final AbstractDataBinder<ArrayAdapter<String>, Tab, ListView, Void> dataBinder) {

        }

        @Override
        public void onFinished(
                @NonNull final AbstractDataBinder<ArrayAdapter<String>, Tab, ListView, Void> dataBinder,
                @NonNull final Tab key, @Nullable final ArrayAdapter<String> data,
                @NonNull final ListView view, @NonNull final Void... params) {
            if (getTab().equals(key)) {
                view.setAdapter(data);
                adapter = data;
                dataBinder.removeListener(this);
            }
        }

        @Override
        public final void saveInstanceState(@NonNull final Bundle outState) {
            if (adapter != null && !adapter.isEmpty()) {
                String[] array = new String[adapter.getCount()];

                for (int i = 0; i < array.length; i++) {
                    array[i] = adapter.getItem(i);
                }

                outState.putStringArray(String.format(ADAPTER_STATE_EXTRA, getTab().getTitle()),
                        array);
            }
        }

        @Override
        public void restoreInstanceState(@Nullable final Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                String key = String.format(ADAPTER_STATE_EXTRA, getTab().getTitle());
                String[] items = savedInstanceState.getStringArray(key);

                if (items != null && items.length > 0) {
                    adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                }
            }
        }

        @Override
        public boolean onLoadTabPreview(@NonNull final TabSwitcher tabSwitcher, @NonNull final Tab tab) {
            return !getTab().equals(tab) || adapter != null;
        }

    }

    private class Decorator extends StatefulTabSwitcherDecorator<State> {

        @Nullable
        @Override
        protected State onCreateState(@NonNull final Context context, @NonNull final TabSwitcher tabSwitcher,
                                      @NonNull final View view, @NonNull final Tab tab, final int index, final int viewType,
                                      @Nullable final Bundle savedInstanceState) {
            if (viewType == 2) {
                State state = new State(tab);
                tabSwitcher.addTabPreviewListener(state);

                if (savedInstanceState != null) {
                    state.restoreInstanceState(savedInstanceState);
                }

                return state;
            }

            return null;
        }

        @Override
        protected void onClearState(@NonNull final State state) {
            tabSwitcher.removeTabPreviewListener(state);
        }

        @Override
        protected void onSaveInstanceState(@NonNull final View view, @NonNull final Tab tab, final int index, final int viewType,
                                           @Nullable final State state, @NonNull final Bundle outState) {
            if (state != null) {
                state.saveInstanceState(outState);
            }
        }

        @NonNull
        @Override
        public View onInflateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup parent, final int viewType) {
            View view;

            view = inflater.inflate(R.layout.tab_default, parent, false);

            Toolbar toolbar = view.findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.tab);
            toolbar.setOnMenuItemClickListener(createToolbarMenuListener());
            Menu menu = toolbar.getMenu();
            TabSwitcher.setupWithMenu(tabSwitcher, menu, createTabSwitcherButtonListener());
            return view;
        }

        @Override
        public void onShowTab(@NonNull final Context context,
                              @NonNull final TabSwitcher tabSwitcher, @NonNull final View view,
                              @NonNull final Tab tab, final int index, final int viewType,
                              @Nullable final State state,
                              @Nullable final Bundle savedInstanceState) {
            TextView textView = findViewById(android.R.id.title);
            textView.setText(tab.getTitle());
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setVisibility(tabSwitcher.isSwitcherShown() ? View.GONE : View.VISIBLE);

        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getViewType(@NonNull final Tab tab, final int index) {
            Bundle parameters = tab.getParameters();
            return parameters != null ? parameters.getInt(VIEW_TYPE_EXTRA) : 0;
        }

    }

    private static class DataBinder extends AbstractDataBinder<ArrayAdapter<String>, Tab, ListView, Void> {

        public DataBinder(@NonNull final Context context) {
            super(context.getApplicationContext());
        }

        @Nullable
        @Override
        protected ArrayAdapter<String> doInBackground(@NonNull final Tab key,
                                                      @NonNull final Void... params) {
            String[] array = new String[10];

            for (int i = 0; i < array.length; i++) {
                array[i] = String.format(Locale.getDefault(), "%s, item %d", key.getTitle(), i + 1);
            }

            try {
                // Simulate a long loading time...
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // There's nothing we can do...
            }

            return new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, array);
        }

        @Override
        protected void onPostExecute(@NonNull final ListView view,
                                     @Nullable final ArrayAdapter<String> data, final long duration,
                                     @NonNull final Void... params) {
            if (data != null) {
                view.setAdapter(data);
            }
        }

    }

    private static final String VIEW_TYPE_EXTRA = MainActivity.class.getName() + "::ViewType";
    private static final String ADAPTER_STATE_EXTRA = State.class.getName() + "::%s::AdapterState";
    private TabSwitcher tabSwitcher;
    private Decorator decorator;
    private Snackbar snackbar;
    private DataBinder dataBinder;


    @NonNull
    private OnApplyWindowInsetsListener createWindowInsetsListener() {
        return (v, insets) -> {
            int left = insets.getSystemWindowInsetLeft();
            int top = insets.getSystemWindowInsetTop();
            int right = insets.getSystemWindowInsetRight();
            int bottom = insets.getSystemWindowInsetBottom();
            tabSwitcher.setPadding(left, top, right, bottom);
            float touchableAreaTop = top;

            if (tabSwitcher.getLayout() == Layout.TABLET) {
                touchableAreaTop += getResources().getDimensionPixelSize(R.dimen.tablet_tab_container_height);
            }

            RectF touchableArea = new RectF(left, touchableAreaTop, getDisplayWidth(MainActivity.this) - right, touchableAreaTop + ThemeUtil.getDimensionPixelSize(MainActivity.this, R.attr.actionBarSize));
            tabSwitcher.addDragGesture(new SwipeGesture.Builder().setTouchableArea(touchableArea).create());
            tabSwitcher.addDragGesture(new PullDownGesture.Builder().setTouchableArea(touchableArea).create());
            return insets;
        };
    }

    @NonNull
    private OnClickListener createAddTabListener() {
        return view -> {
            int index = tabSwitcher.getCount();
            Animation animation = createRevealAnimation();
            tabSwitcher.addTab(createTab(index), 0, animation);
        };
    }

    @NonNull
    private OnMenuItemClickListener createToolbarMenuListener() {
        return item -> {
            switch (item.getItemId()) {
                case R.id.add_tab_menu_item:
                    int index = tabSwitcher.getCount();
                    Tab tab = createTab(index);

                    if (tabSwitcher.isSwitcherShown()) {
                        tabSwitcher.addTab(tab, 0, createRevealAnimation());
                    } else {
                        tabSwitcher.addTab(tab, 0, createPeekAnimation());
                    }

                    return true;
                case R.id.clear_tabs_menu_item:
                    tabSwitcher.clear();
                    return true;
                default:
                    return false;
            }
        };
    }

    private void inflateMenu() {
        tabSwitcher.inflateToolbarMenu(tabSwitcher.getCount() > 0 ? R.menu.tab_switcher : R.menu.tab, createToolbarMenuListener());
    }

    @NonNull
    private OnClickListener createTabSwitcherButtonListener() {
        return view -> tabSwitcher.toggleSwitcherVisibility();
    }

    @NonNull
    private AddTabButtonListener createAddTabButtonListener() {
        return tabSwitcher -> {
            int index = tabSwitcher.getCount();
            Tab tab = createTab(index);
            tabSwitcher.addTab(tab, 0);
        };
    }

    @NonNull
    private OnClickListener createUndoSnackbarListener(@NonNull final Snackbar snackbar,
                                                       final int index, @NonNull final Tab... tabs) {
        return view -> {
            snackbar.setAction(null, null);

            if (tabSwitcher.isSwitcherShown()) {
                tabSwitcher.addAllTabs(tabs, index);
            } else if (tabs.length == 1) {
                tabSwitcher.addTab(tabs[0], 0, createPeekAnimation());
            }

        };
    }

    @NonNull
    private BaseTransientBottomBar.BaseCallback<Snackbar> createUndoSnackbarCallback(
            final Tab... tabs) {
        return new BaseTransientBottomBar.BaseCallback<Snackbar>() {

            @Override
            public void onDismissed(final Snackbar snackbar, final int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    for (Tab tab : tabs) {
                        tabSwitcher.clearSavedState(tab);
                        decorator.clearState(tab);
                    }
                }
            }
        };
    }

    private void showUndoSnackbar(@NonNull final CharSequence text, final int index, @NonNull final Tab... tabs) {
        snackbar = Snackbar.make(tabSwitcher, text, Snackbar.LENGTH_LONG).setActionTextColor(
                ContextCompat.getColor(this, R.color.snackbar_action_text_color));
        snackbar.setAction(R.string.undo, createUndoSnackbarListener(snackbar, index, tabs));
        snackbar.addCallback(createUndoSnackbarCallback(tabs));
        snackbar.show();
    }

    @NonNull
    private Animation createRevealAnimation() {
        float x = 0;
        float y = 0;
        View view = getNavigationMenuItem();

        if (view != null) {
            int[] location = new int[2];
            view.getLocationInWindow(location);
            x = location[0] + (view.getWidth() / 2f);
            y = location[1] + (view.getHeight() / 2f);
        }

        return new RevealAnimation.Builder().setX(x).setY(y).create();
    }

    @NonNull
    private Animation createPeekAnimation() {
        return new PeekAnimation.Builder().setX(tabSwitcher.getWidth() / 2f).create();
    }

    @Nullable
    private View getNavigationMenuItem() {
        Toolbar[] toolbars = tabSwitcher.getToolbars();

        if (toolbars != null) {
            Toolbar toolbar = toolbars.length > 1 ? toolbars[1] : toolbars[0];
            int size = toolbar.getChildCount();

            for (int i = 0; i < size; i++) {
                View child = toolbar.getChildAt(i);

                if (child instanceof ImageButton) {
                    return child;
                }
            }
        }

        return null;
    }

    @NonNull
    private Tab createTab(final int index) {
        CharSequence title = getString(R.string.tab_title, index + 1);
        Tab tab = new Tab(title);
        Bundle parameters = new Bundle();
        parameters.putInt(VIEW_TYPE_EXTRA, index % 3);
        tab.setParameters(parameters);
        return tab;
    }

    @Override
    public final void onSwitcherShown(@NonNull final TabSwitcher tabSwitcher) {

    }

    @Override
    public final void onSwitcherHidden(@NonNull final TabSwitcher tabSwitcher) {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    public final void onSelectionChanged(@NonNull final TabSwitcher tabSwitcher,
                                         final int selectedTabIndex,
                                         @Nullable final Tab selectedTab) {

    }

    @Override
    public final void onTabAdded(@NonNull final TabSwitcher tabSwitcher, final int index,
                                 @NonNull final Tab tab, @NonNull final Animation animation) {
        inflateMenu();
        TabSwitcher.setupWithMenu(tabSwitcher, createTabSwitcherButtonListener());
    }

    @Override
    public final void onTabRemoved(@NonNull final TabSwitcher tabSwitcher, final int index,
                                   @NonNull final Tab tab, @NonNull final Animation animation) {
        CharSequence text = getString(R.string.removed_tab_snackbar, tab.getTitle());
        showUndoSnackbar(text, index, tab);
        inflateMenu();
        TabSwitcher.setupWithMenu(tabSwitcher, createTabSwitcherButtonListener());
    }

    @Override
    public final void onAllTabsRemoved(@NonNull final TabSwitcher tabSwitcher,
                                       @NonNull final Tab[] tabs,
                                       @NonNull final Animation animation) {
        CharSequence text = getString(R.string.cleared_tabs_snackbar);
        showUndoSnackbar(text, 0, tabs);
        inflateMenu();
        TabSwitcher.setupWithMenu(tabSwitcher, createTabSwitcherButtonListener());
    }

    @Override
    public final void setTheme(final int resId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themeKey = getString(R.string.theme_preference_key);
        String themeDefaultValue = getString(R.string.theme_preference_default_value);
        int theme = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString(themeKey, themeDefaultValue)));

        if (theme != 0) {
            super.setTheme(R.style.AppTheme_Translucent_Dark);
        } else {
            super.setTheme(R.style.AppTheme_Translucent_Light);
        }
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBinder = new DataBinder(this);
        decorator = new Decorator();
        tabSwitcher = findViewById(R.id.tab_switcher);
        tabSwitcher.clearSavedStatesWhenRemovingTabs(false);
        ViewCompat.setOnApplyWindowInsetsListener(tabSwitcher, createWindowInsetsListener());
        tabSwitcher.setDecorator(decorator);
        tabSwitcher.addListener(this);
        tabSwitcher.showToolbars(true);

        tabSwitcher.addTab(createTab(0));

        tabSwitcher.showAddTabButton(createAddTabButtonListener());
        tabSwitcher.setToolbarNavigationIcon(R.drawable.ic_plus_24dp, createAddTabListener());
        TabSwitcher.setupWithMenu(tabSwitcher, createTabSwitcherButtonListener());
        inflateMenu();
    }

}
