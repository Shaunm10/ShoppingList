package com.example.shoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class ShoppingList {
}

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)


@Composable
fun ShoppingListApp() {
    // State
    var items by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("1") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                showDialog = true
            },
            //Text(text = "click me."),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // desity pixels
        ) {
            items(items) { shoppingItem ->
                // create a new ShoppingListItem for each of the items in our list (state)
                if (shoppingItem.isEditing) {

                    // display this UI
                    ShoppingItemEditor(
                        item = shoppingItem,
                        onEditComplete = { editedName, editedQuantity ->

                            // making isEditing false for all items.
                            items = items.map { it.copy(isEditing = false) }

                            // find the item being that is being edited
                            val editedItem = items.find { it.id == shoppingItem.id }

                            // finally update the two properties of the item being edited.
                            editedItem?.let {
                                it.name = editedName
                                it.quantity = editedQuantity
                            }
                        })
                } else {
                    ShoppingListItem(item = shoppingItem, onEditClick = {

                        // copy the items over and set the isEditing to all false but this particular one.
                        items = items.map {
                            it.copy(
                                // will only set isEditing for the 1 item being edited.
                                isEditing = (it.id == shoppingItem.id)
                            )
                        }
                    }, onDeleteClick = {
                        // now remove this item
                        items = items - shoppingItem

                    })
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Add Button
                    Button(onClick = {
                        // only if the user added text.
                        if (itemName.isNotBlank()) {

                            val shoppingItem = ShoppingItem(
                                // this is flawed logic
                                id = items.count() + 1,
                                name = itemName,
                                quantity = itemQuantity.toIntOrNull() ?: 1
                            )

                            // this adds the item to the list.
                            items = items + shoppingItem


                            // reset the state
                            itemName = ""
                            itemQuantity = "1"
                            showDialog = false
                        }

                    }) {
                        Text(text = "Add")
                    }
                    // Cancel Button
                    Button(onClick = {
                        itemName = ""
                        itemQuantity = "1"
                        showDialog = false
                    }) {
                        Text(text = "Cancel")
                    }
                }
            },
            title = { Text("Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,

                        onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            itemQuantity = it
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun ShoppingItemEditor(
    item: ShoppingItem,
    onEditComplete: (String, Int) -> Unit
) {
    var editingName by remember { mutableStateOf(item.name) }
    var editingQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            BasicTextField(
                value = editingName,
                onValueChange = { input -> editingName = input },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)

            )
            BasicTextField(
                value = editingQuantity,
                onValueChange = { input -> editingQuantity = input },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }
        Button(onClick = {
            isEditing = false
            onEditComplete(editingName, editingQuantity.toIntOrNull() ?: 1)
        }) {
            Text(text = "Save")
        }
    }

}

@Composable
fun ShoppingListItem(
    /* This is basically the interface for the composable*/
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0XFF018786)),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(
            text = item.name,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Qty: ${item.quantity}",
            modifier = Modifier.padding(8.dp)
        )
        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick)
            {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }// end Row
    }// end Row
}