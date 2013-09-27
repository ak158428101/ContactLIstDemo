package net.wszf.client.contact;

import java.util.ArrayList;
import java.util.List;

import net.wszf.client.contact.domain.ContactDomain;
import net.wszf.client.contact.view.RefreshableView;
import net.wszf.client.contact.view.RefreshableView.PullToRefreshListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends Activity
	{
		private List<ContactDomain> cdList = new ArrayList<ContactDomain>();
		private List<ContactDomain>cdbfList=new ArrayList<ContactDomain>();
		private CommonAdapter adapter;
		private ListView listView;
		private RefreshableView refreshableView;
		private int max = 13;
		private int currentCount = 0;
		private int lastItemNum;
		private boolean ispull=false;
		private boolean isnull = false;
		private static final String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER };
		private LinearLayout pagingLoading;
		private boolean isThreadRun = false;
		private Handler handler = new Handler(new Handler.Callback()
			{
				@Override
				public boolean handleMessage(Message msg)
					{
						switch (msg.what)
							{
							case 0:
								if (cdList.size() > 0)
									{
										if (!isnull)
											{
												listView.removeFooterView(pagingLoading);
												listView.addFooterView(pagingLoading);
											} else
											{
												listView.removeFooterView(pagingLoading);
											}
										listView.setAdapter(adapter);
									} else
									{

									}

								break;
							case 1:
								ispull=false;
								refreshableView.finishRefreshing();
								cdList.clear();
								cdList.addAll(cdbfList);
								adapter.notifyDataSetChanged();
								break;
							default:
								if (isnull)
									{
										listView.removeFooterView(pagingLoading);
									}
								adapter.notifyDataSetChanged();

								break;
							}
						return false;
					}
			});

		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				fillView();
				pagingLoading = (LinearLayout) LinearLayout.inflate(this, R.layout.fullscreen_loading_indicator, null);
				// getContact(cdList,first,max);
				new ContactTask().execute(0, currentCount, max);
			}

		private void fillView()
			{
				listView = (ListView) findViewById(R.main.listview);
				refreshableView = (RefreshableView) findViewById(R.main.refreshable_view);
				adapter = new CommonAdapter(this, cdList);
				listView.setAdapter(adapter);
				listView.setOnScrollListener(onScrollListener);
				refreshableView.setOnRefreshListener(new PullToRefreshListener()
					{
						@Override
						public void onRefresh()
							{
								//cdList.clear();
								ispull=true;
								cdbfList.clear();
								 new ContactTask().execute(1,0,currentCount);
							}
					}, 0);
			}

		@Override
		public boolean onCreateOptionsMenu(Menu menu)
			{
				// Inflate the menu; this adds items to the action bar if it is present.
				getMenuInflater().inflate(R.menu.main, menu);
				return true;
			}

		private void getContact(List<ContactDomain> cdList, int first, int max)
			{
				ContentResolver cr = this.getContentResolver();
				Cursor resultCursor = cr.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, "_id  limit " + first + ","
						+ max);
				if (resultCursor != null && resultCursor.getCount() != 0)
					{
						while (resultCursor.moveToNext())
							{
								ContactDomain cd = new ContactDomain();
								// 得到手机号码
								cd.name = resultCursor.getString(0);
								cd.phoneNum = resultCursor.getString(1);
								if(ispull)
									{
										cdbfList.add(cd);
									}
								else{
									cdList.add(cd);
								}
							}
						if(!ispull)
							{
								currentCount += resultCursor.getCount();
							}
					} else
					{
						isnull = true;
					}
			}

		private OnScrollListener onScrollListener = new OnScrollListener()
			{
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState)
					{
						if (currentCount == lastItemNum && scrollState == OnScrollListener.SCROLL_STATE_IDLE)
							{
								if (!isnull && !isThreadRun)
									{
										new ContactTask().execute(2, currentCount, max);
									}
							}
					}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
					{
						lastItemNum = firstVisibleItem + visibleItemCount - 1;
						System.out.println("lastItemNum:" + lastItemNum + "totalItemCount:" + totalItemCount);
					}
			};

		class ContactTask extends AsyncTask<Integer, Void, Void>
			{
				int what;

				@Override
				protected void onPostExecute(Void result)
					{
						handler.sendEmptyMessage(what);
						System.out.println("isnull:"+isnull);
						isThreadRun = false;
						super.onPostExecute(result);
					}

				@Override
				protected Void doInBackground(Integer... params)
					{
						what = params[0];
						isThreadRun = true;
						getContact(cdList, params[1], params[2]);
						return null;
					}
			}
	}
