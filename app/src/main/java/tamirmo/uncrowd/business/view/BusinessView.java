package tamirmo.uncrowd.business.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.databinding.BusinessViewBinding;

public class BusinessView extends ConstraintLayout {
    private BusinessViewBinding binding;

    public BusinessView(Context context) {
        super(context);
        init(context);
    }

    public BusinessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BusinessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        binding = BusinessViewBinding.inflate(layoutInflater, this, true);
    }

    public void setBusiness(Business business){

        if(binding != null){
            binding.setBusiness(business);
            binding.executePendingBindings();
        }
    }
}
