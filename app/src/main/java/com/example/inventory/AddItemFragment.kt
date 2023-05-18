package com.example.inventory

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Item
import com.example.inventory.databinding.FragmentAddItemBinding

/**
 * Fragmen untuk menambah atau memperbarui item di basis data Inventaris.
 */
class AddItemFragment : Fragment() {

    // Gunakan delegasi properti Kotlin 'by activityViewModels()' dari
    // artefak fragmen-ktx untuk membagikan ViewModel di seluruh fragmen.
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database
                .itemDao()
        )
    }

    lateinit var item: Item
    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    // Mengikat instance objek yang sesuai dengan tata letak fragment_add_item.xml
    // Properti ini bukan nol antara callback siklus hidup onCreateView() dan onDestroyView(),
    // saat hierarki tampilan dilampirkan ke fragmen
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    /**
     * Inisialisasi variabel binding
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Mengembalikan nilai true jika EditTexts tidak kosong
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemPrice.text.toString(),
            binding.itemCount.text.toString()
        )
    }

    /**
     * Mengikat tampilan dengan informasi item yang diteruskan.
     */
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.itemName.text.toString(),
                binding.itemPrice.text.toString(),
                binding.itemCount.text.toString(),
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Dipanggil saat tampilan dibuat.
     * Argumen navigasi itemId menentukan item edit atau tambahkan item baru.
     * Jika itemId positif, metode ini mengambil informasi dari database dan mengizinkan pengguna untuk memperbaruinya.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewItem()
            }
        }
    }

    /**
     * Dipanggil sebelum fragmen dihancurkan.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Sembunyikan keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    /**
     * Mengikat tampilan dengan informasi item yang diteruskan.
     */
    private fun bind(item: Item) {
        val price = "%.2f".format(item.itemPrice)
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            itemPrice.setText(price, TextView.BufferType.SPANNABLE)
            itemCount.setText(item.quantityInStock.toString(), TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateItem() }
        }
    }

    /**
     * Memperbarui Item yang ada di database dan menavigasi ke daftar fragmen.
     */
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                this.navigationArgs.itemId,
                this.binding.itemName.text.toString(),
                this.binding.itemPrice.text.toString(),
                this.binding.itemCount.text.toString()
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }
}