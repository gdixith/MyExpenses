package org.totschnig.myexpenses.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.totschnig.myexpenses.R;
import org.totschnig.myexpenses.dialog.EditTextDialog;
import org.totschnig.myexpenses.model.CurrencyEnum;
import org.totschnig.myexpenses.model.Money;
import org.totschnig.myexpenses.provider.DatabaseConstants;

import java.util.Currency;
import java.util.Locale;

public class CurrencyList extends ListFragment {

  private CurrencyEnum[] sortedValues;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    sortedValues = CurrencyEnum.sortedValues();
    setAdapter();
  }

  private void setAdapter() {
    ArrayAdapter<CurrencyEnum> curAdapter = new ArrayAdapter<CurrencyEnum>(
        getActivity(), android.R.layout.simple_list_item_1, sortedValues) {
      @NonNull
      @Override
      public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        String text;
        TextView v = (TextView) super.getView(position, convertView, parent);
        CurrencyEnum item = sortedValues[position];
        try {
          Currency c = Currency.getInstance(item.name());
          text = String.format(Locale.getDefault(), "%s, %d", Money.getSymbol(c), Money.getFractionDigits(c));
        } catch (IllegalArgumentException e) {
          text = "not supported by your OS";
        }
        //noinspection SetTextI18n
        v.setText(String.format(Locale.getDefault(), "%s (%s)", v.getText(), text));
        return v;
      }
    };

    setListAdapter(curAdapter);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    CurrencyEnum item = sortedValues[position];
    try {
      Currency c = Currency.getInstance(item.name());
      Bundle args = new Bundle();
      args.putString(EditTextDialog.KEY_DIALOG_TITLE, getString(R.string.dialog_title_set_fraction_digits));
      args.putString(DatabaseConstants.KEY_CURRENCY, item.name());
      args.putString(EditTextDialog.KEY_VALUE, String.valueOf(String.valueOf(Money.getFractionDigits(c))));
      args.putInt(EditTextDialog.KEY_INPUT_TYPE, InputType.TYPE_CLASS_NUMBER);
      EditTextDialog.newInstance(args).show(getFragmentManager(), "SET_FRACTION_DIGITS");
    } catch (IllegalArgumentException e) {
      // "not supported by your OS";
    }
  }
}
