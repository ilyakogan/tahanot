package com.tahanot.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.tahanot.R;
import com.tahanot.ResourceSaver;
import com.tahanot.persistence.WidgetPersistence;
import com.tahanot.widgetupdate.WidgetUpdateService;

public class WidgetContextMenu extends Activity {

	private int mWidgetId;
	Activity mActivity = this;

	@Override
	protected void onResume() {
		super.onResume();

		Intent intent = getIntent();
		mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        // TODO: Analytics
//        EasyTracker.getInstance().setContext(mActivity);
//		EasyTracker.getTracker().sendView("Activity-WidgetContextMenu");
		
		showDialog();
	}

	ClickHandler[] mClickHandlers = { new UpdateWidgetClickHandler(), new ChangeNameClickHandler(), new SelectDifferentStopClickHandler(), new AboutClickHandler(), new ShareClickHandler() };

	private void showDialog() {
		CharSequence[] items = { getString(R.string.menu_item_update), getString(R.string.menu_item_change_stop_name),
				getString(R.string.menu_item_select_different_stop), getString(R.string.menu_item_about), getString(R.string.menu_item_share) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.context_menu_title).setItems(items, onClickListener).setCancelable(true).setOnCancelListener(onCancelListener);
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			finish();
		}
	};

	private DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			try {
				mClickHandlers[which].onClick();
			} catch (Exception ex) {
				finish();
			}

		}
	};

	interface ClickHandler {
		void onClick();
	}

	class UpdateWidgetClickHandler implements ClickHandler {

		@Override
		public void onClick() {
			ResourceSaver.userJustDidSomething();
            // TODO: Analytics
//            EasyTracker.getInstance().setContext(mActivity);
//			EasyTracker.getTracker().sendView("WidgetContextMenu-UpdateWidget");

			// If for some reason the widget is missing from the list of widgets to update, add it again.
			new WidgetPersistence().addWidget(mWidgetId);

			Intent intent = new Intent(mActivity, WidgetUpdateService.class);
			intent.putExtra("forceUpdateEvenIfTooFrequent", true);
			intent.putExtra("reactivate", true);
			startService(intent);
			finish();
		}
	}

	class ChangeNameClickHandler implements ClickHandler {

		@Override
		public void onClick() {
			ResourceSaver.userJustDidSomething();
            // TODO: Analytics
//            EasyTracker.getInstance().setContext(mActivity);
//			EasyTracker.getTracker().sendView("WidgetContextMenu-ChangeName");

			final WidgetPersistence pers = new WidgetPersistence();
			int stopCode = pers.getStopCode(mWidgetId);
			String stopRealName = pers.getStopRealName(mWidgetId);
			String previousStopDisplayName = pers.getStopDisplayName(mWidgetId);

			// Fix old widgets
			if (stopRealName == null) {
				pers.setStopRealName(mWidgetId, previousStopDisplayName);
				stopRealName = previousStopDisplayName;
			}

			String format = mActivity.getString(R.string.change_stop_name_description);
			String description = String.format(format, stopCode, stopRealName);

			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			final EditText stopNameInput = new EditText(mActivity);
			stopNameInput.setText(previousStopDisplayName);
			stopNameInput.setSelectAllOnFocus(true);

			OnClickListener onOkClickListener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					hideKeyboard(stopNameInput);
					CharSequence newDisplayName = stopNameInput.getText();
					pers.setStopDisplayName(mWidgetId, newDisplayName.toString());
					finish();
				}
			};

			OnClickListener onCancelClickListener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					hideKeyboard(stopNameInput);
					onCancelListener.onCancel(dialog);
				}
			};

			builder.setTitle(R.string.title_change_stop_name) //
					.setView(stopNameInput) //
					.setMessage(description) //
					.setPositiveButton(R.string.ok, onOkClickListener) //
					.setNegativeButton(R.string.cancel, onCancelClickListener) //
					.setCancelable(true).setOnCancelListener(onCancelListener);
			Dialog dialog = builder.create();
			dialog.show();

			stopNameInput.requestFocus();
			showKeyboard();
		}

		private void showKeyboard() {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		}

		private void hideKeyboard(View view) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	class SelectDifferentStopClickHandler implements ClickHandler {

		@Override
		public void onClick() {
			ResourceSaver.userJustDidSomething();
            // TODO: Analytics
//            EasyTracker.getInstance().setContext(mActivity);
//			EasyTracker.getTracker().sendView("WidgetContextMenu-ChangeStop");

			Intent intent = new Intent(mActivity, StopSelection.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
			startActivity(intent);
			finish();
		}
	}

	class AboutClickHandler implements ClickHandler {

		@Override
		public void onClick() {
			ResourceSaver.userJustDidSomething();
            // TODO: Analytics
//			EasyTracker.getTracker().sendView("WidgetContextMenu-About");
//            EasyTracker.getInstance().setContext(mActivity);

			Intent intent = new Intent(mActivity, Landing.class);
			intent.putExtra(Landing.SELECT_TAB_PARAM, Landing.TAB_ABOUT);
			startActivity(intent);
			finish();
		}
	}

	class ShareClickHandler implements ClickHandler {

		@Override
		public void onClick() {
			ResourceSaver.userJustDidSomething();
            // TODO: Analytics
//			EasyTracker.getTracker().sendView("WidgetContextMenu-Share");
//            EasyTracker.getInstance().setContext(mActivity);

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			//sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title));
			sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			startActivity(Intent.createChooser(sendIntent, getString(R.string.share_title)));
			finish();
		}
	}
}
