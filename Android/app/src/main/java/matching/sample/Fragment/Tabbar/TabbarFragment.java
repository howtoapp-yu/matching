package matching.sample.Fragment.Tabbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import matching.sample.Fragment.BaseFragment;
import matching.sample.Fragment.Matching.MatchingFragment;
import matching.sample.Fragment.MyPage.MyPageFragment;
import matching.sample.Fragment.User.UserList.UserListFragment;
import matching.sample.R;

public class TabbarFragment extends BaseFragment {

    // ユーザ一覧
    private UserListFragment mUserListFragment;

    // マッチング
    private MatchingFragment mMatchingFragment;

    // マイページ
    private MyPageFragment mMyPageFragment;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_tabbar, null);

        // 主要コンテンツ画面を初期化する
        initFragments();

        // ボタンイベント
        initAction(view);

        return view;
    }

    // 主要コンテンツ画面を初期化する
    private void initFragments() {

        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // ユーザ一覧
        mMyPageFragment = new MyPageFragment();
        transaction.add(R.id.tabContainerLayout, mMyPageFragment);

        // マッチング
        mMatchingFragment = new MatchingFragment();
        transaction.add(R.id.tabContainerLayout, mMatchingFragment);

        // マイページ
        mUserListFragment = new UserListFragment();
        transaction.add(R.id.tabContainerLayout, mUserListFragment);

        transaction.commit();
    }

    // ボタンイベントを設定する
    private void initAction(View view) {

        // tab0が選択された
        view.findViewById(R.id.tab0Layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(0);
            }
        });

        // tab1が選択された
        view.findViewById(R.id.tab1Layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(1);
            }
        });

        // tab2が選択された
        view.findViewById(R.id.tab2Layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTab(2);
            }
        });
    }

    // タブを切り替える
    private void changeTab(int index) {

        View view = getView();

        // 表示を切り替え
        mUserListFragment.getView().setVisibility((index == 0)
                ? View.VISIBLE
                : View.GONE);

        mMatchingFragment.getView().setVisibility((index == 1)
                ? View.VISIBLE
                : View.GONE);

        mMyPageFragment.getView().setVisibility((index == 2)
                ? View.VISIBLE
                : View.GONE);

        // タブボタンの画像を差し替え
        ((ImageView)view.findViewById(R.id.tab0ImageView)).setImageResource((index == 0)
                ? R.drawable.tab0_on
                : R.drawable.tab0_off);

        ((ImageView)view.findViewById(R.id.tab1ImageView)).setImageResource((index == 1)
                ? R.drawable.tab1_on
                : R.drawable.tab1_off);
        ((ImageView)view.findViewById(R.id.tab2ImageView)).setImageResource((index == 2)
                ? R.drawable.tab2_on
                : R.drawable.tab2_off);
    }

    // 画面を更新する
    public void reload() {

        mUserListFragment.reload();
        mMatchingFragment.reload();
        mMyPageFragment.reload();
    }
}
