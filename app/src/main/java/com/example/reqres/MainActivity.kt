package com.example.reqres

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reqres.ui.theme.ReqResTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReqResTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    var userList = remember { mutableStateListOf<User>() }
                    val userListState = rememberLazyListState()
                    var currentPage by remember { mutableStateOf(1) }

                    LaunchedEffect(currentPage) {
                        withContext(Dispatchers.IO) {
                            userList.addAll(ApiService.GetUsers(currentPage))
                        }
                    }

                    LaunchedEffect(userListState) {
                        snapshotFlow { userListState.layoutInfo }
                            .collect {layoutInfo ->
                                val visibleItemCount = layoutInfo.visibleItemsInfo.size
                                val totalItemCount = layoutInfo.totalItemsCount
                                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index

                                if(lastVisibleItemIndex == totalItemCount - 1 && visibleItemCount < totalItemCount) {
                                   currentPage += 1
                                }
                            }
                    }

                    Box(
                        modifier = Modifier.padding(innerPadding).fillMaxSize().background(color = colorResource(R.color.black))
                    ) {
                        LazyColumn (
                            state = userListState,
                            modifier = Modifier.padding(15.dp)
                        ) {
                            items(userList) {
                                Spacer(Modifier.padding(top = 15.dp))
                                Box (
                                    modifier = Modifier.fillParentMaxWidth().background(colorResource(R.color.black2), shape = RoundedCornerShape(12.dp))
                                ) {
                                    Row (
                                        modifier = Modifier.padding(15.dp)
                                    ) {
                                        if (it.avatar == null) {
                                            Image(
                                                painter = painterResource(R.drawable.ic_launcher_background),
                                                contentDescription = ""
                                            )
                                        } else {
                                            Image(
                                                bitmap = it.avatar.asImageBitmap(),
                                                contentDescription = "",
                                                modifier = Modifier.size(125.dp).clip(
                                                    RoundedCornerShape(12.dp)
                                                ),
                                                contentScale = ContentScale.Crop,
                                            )
                                        }

                                        Spacer(Modifier.padding(start = 15.dp))

                                      Column {
                                          Text(
                                              text = "First name : ${it.first_name}",
                                              color = colorResource(R.color.white),
                                              fontSize = 14.sp,
                                          )

                                          Text(
                                              text = "Last name : ${it.last_name}",
                                              color = colorResource(R.color.white),
                                              modifier = Modifier.padding(top = 5.dp),
                                              fontSize = 14.sp,
                                          )
                                          Text(
                                              text = "Email : ${it.email}",
                                              color = Color.Gray,
                                              modifier = Modifier.padding(top = 2.dp),
                                              fontSize = 12.sp,
                                          )

                                      }
                                    }
                                }
                            }

                            item {
                                if (userListState.isScrollInProgress) {
                                    Box (
                                        modifier = Modifier.fillParentMaxWidth()
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(15.dp).align(Alignment.BottomCenter)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
