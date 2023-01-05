package me.xianglun.blinkblinkbeachadmin.ui.report

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.xianglun.blinkblinkbeachadmin.data.model.Report
import me.xianglun.blinkblinkbeachadmin.databinding.ReportCardTemplateBinding

class ReportAdapter(
    private val reportList: List<Report>,
    private val context: Context,
    private val navController: NavController
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    class ViewHolder(val binding: ReportCardTemplateBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = ReportCardTemplateBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reportList[position]
        holder.binding.apply {
            Glide.with(context)
                .load(Uri.parse(report.imageUrl))
                .into(reportCardImageView)
            reportDateTimeTextView.text = report.date
            reportIdText.text = report.id
            reportLocationText.text =
                report.latitude.toString() + ", " + report.longitude.toString()
            root.setOnClickListener {
                val action = ReportFragmentDirections.actionReportFragmentToReportDetailFragment(report)
                navController.navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

}