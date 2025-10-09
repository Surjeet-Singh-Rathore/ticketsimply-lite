package com.bitla.ts.presentation.adapter.sellfAuditFormAdapters


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.restaurant_app.presentation.utils.gone
import com.bitla.restaurant_app.presentation.utils.toast
import com.bitla.ts.databinding.AdapterAuditQuestionsBinding
import com.bitla.ts.domain.pojo.self_audit_question.response.Result
import visible


class SelfAuditQuestionsAdapter(
    private val context: Context,
    private val questionData: Result,
    private val onItemClick:((optionId: String, stageId: String, questionId:String, questionType:String)->Unit),
    private val onNormalItemClick:((optionId: String, questionId:String, questionType:String)->Unit)
) :
    RecyclerView.Adapter<SelfAuditQuestionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterAuditQuestionsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return questionData.option_questions.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem= questionData.option_questions.get(position)

        if(currentItem.type.equals("Normal", true)){
            holder.questionTextView.text=currentItem.question
            holder.bpdpQuestionLayout.gone()
            holder.normalQuestionLayout.visible()

            currentItem.options.forEachIndexed { index, option ->
                val radioButton = RadioButton(context)
                radioButton.id = option.id.toInt() // Unique ID for each RadioButton
                radioButton.text = option.title
                radioButton.setPadding(0,4,0,4)
                radioButton.setOnClickListener {
                    onNormalItemClick.invoke( option.id, currentItem.question_id, currentItem.type)

                }
                holder.optionsRG.addView(radioButton)
            }
            holder.dropdownExpand.setOnClickListener{
                holder.dropdownExpand.gone()
                holder.dropdownCollapse.visible()
                holder.optionsRG.visible()

            }
            holder.dropdownCollapse.setOnClickListener{
                holder.dropdownCollapse.gone()
                holder.dropdownExpand.visible()
                holder.optionsRG.gone()
            }

        }else{
            holder.questionCityTV.text=currentItem.question
            holder.normalQuestionLayout.gone()
            holder.cityRV.gone()
            holder.bpdpQuestionLayout.visible()

            val adapter = SelfAuditOptionCityAdapter(
                context,
                questionData.boarding_data,
                currentItem.options
            ){optionId: String, stageId: String ->
                onItemClick.invoke(optionId,stageId, currentItem.question_id, currentItem.type)
            }
            holder.cityRV.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            holder.cityRV.adapter = adapter


            holder.dropdownCityExpand.setOnClickListener{
                holder.dropdownCityExpand.gone()
                holder.dropdownCityCollapse.visible()
                holder.cityRV.visible()

            }
            holder.dropdownCityCollapse.setOnClickListener{
                holder.dropdownCityCollapse.gone()
                holder.dropdownCityExpand.visible()
                holder.cityRV.gone()

            }

        }



    }

    class ViewHolder(binding: AdapterAuditQuestionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val optionsRG=binding.radiogroup
        val questionTextView=binding.questionTV
//        val optionRecyclerView=binding.optionsRV
        val dropdownExpand=binding.dropdownExpand
        val dropdownCityExpand=binding.dropdownCityExpand
        val dropdownCollapse=binding.dropdownCollapse
        val dropdownCityCollapse=binding.dropdownCityCollapse
        val normalQuestionLayout=binding.normalQuestionCL
        val bpdpQuestionLayout=binding.bpdpQuestionCL
        val questionCityTV=binding.questionCityTV
        val cityRV=binding.cityRV
//        val ratingTitle=binding.ratingTitleTV
//        val ratingRadioGroup=binding.ratingRadioGroup
//        val ratingCL=binding.ratingCL
//        val ratingExpand=binding.dropdownRatingExpand
//        val ratingCollapse=binding.dropdownRatingCollapse
    }


}