package net.wszf.client.contact;

import java.util.List;

import net.wszf.client.contact.domain.ContactDomain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommonAdapter extends BaseAdapter
	{
		private Context context;
		private LayoutInflater layoutInflater;
		private List<ContactDomain> list;
		
	public CommonAdapter(Context context,List<ContactDomain> list)
			{
				super();
				this.context = context;
				this.layoutInflater = LayoutInflater.from(context);
				this.list = list;
			}

	@Override
	public int getCount()
		{
			// TODO Auto-generated method stub
			return list.size();
		}

	@Override
	public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return list.get(position);
		}

	@Override
	public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return 0;
		}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			WrapperView wrapperView;
			ContactDomain cd=(ContactDomain) getItem(position);
			if(convertView==null)
				{
					convertView=layoutInflater.inflate(R.layout.contact_list_item, null);
					wrapperView=new WrapperView();
					wrapperView.name_TextView=(TextView) convertView.findViewById(R.item.name_TextView);
					wrapperView.phoneNum_TextView=(TextView) convertView.findViewById(R.item.phoneNum_TextView);
					convertView.setTag(wrapperView);
				}
			else{
				wrapperView=(WrapperView) convertView.getTag();
			}
			wrapperView.name_TextView.setText(cd.getName());
			wrapperView.phoneNum_TextView.setText(cd.getPhoneNum());
			return convertView;
		}

		static class WrapperView
		{
			TextView name_TextView,phoneNum_TextView;
		}

	}
