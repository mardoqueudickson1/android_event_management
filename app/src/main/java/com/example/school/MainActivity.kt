package com.example.school

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigator()
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    var postList by remember { mutableStateOf(emptyList<Post>()) }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeScreen(navController, postList, onUpdatePosts = { postList = it }) }
        composable("edit/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")?.toIntOrNull()
            EditPostScreen(navController, postId, onPostUpdated = { postList = it })
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        navController.navigate("home")
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Bem-vindo ao Gerenciador de Posts")
    }
}

@Composable
fun HomeScreen(navController: NavHostController, postList: List<Post>, onUpdatePosts: (List<Post>) -> Unit) {
    val context = LocalContext.current
    val requestQueue = Volley.newRequestQueue(context)

    LaunchedEffect(Unit) {
        val url = "https://jsonplaceholder.typicode.com/posts"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                val posts = mutableListOf<Post>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    posts.add(Post(obj.getInt("id"), obj.getString("title"), obj.getString("body")))
                }
                onUpdatePosts(posts)
            },
            { it.printStackTrace() }
        )
        requestQueue.add(request)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { navController.navigate("edit/new") }) { Text("Criar Novo Post") }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(postList) { post ->
                PostItem(post, onDelete = {
                    val url = "https://jsonplaceholder.typicode.com/posts/${post.id}"
                    val deleteRequest = JsonObjectRequest(Request.Method.DELETE, url, null,
                        { onUpdatePosts(postList.filter { it.id != post.id }) },
                        { it.printStackTrace() })
                    requestQueue.add(deleteRequest)
                }, onEdit = { navController.navigate("edit/${post.id}") })
            }
        }
    }
}

@Composable
fun EditPostScreen(navController: NavHostController, postId: Int?, onPostUpdated: (List<Post>) -> Unit) {
    val context = LocalContext.current
    val requestQueue = Volley.newRequestQueue(context)
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        if (postId != null) {
            val url = "https://jsonplaceholder.typicode.com/posts/$postId"
            val request = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    title = response.getString("title")
                    body = response.getString("body")
                },
                { it.printStackTrace() })
            requestQueue.add(request)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
        OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Conteúdo") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val url = if (postId == null) "https://jsonplaceholder.typicode.com/posts" else "https://jsonplaceholder.typicode.com/posts/$postId"
            val method = if (postId == null) Request.Method.POST else Request.Method.PUT
            val json = JSONObject().apply {
                put("title", title)
                put("body", body)
                put("userId", 1)  // Simulação
            }
            val request = JsonObjectRequest(method, url, json,
                {
                    navController.navigate("home")
                },
                { it.printStackTrace() })
            requestQueue.add(request)
        }) {
            Text(if (postId == null) "Criar" else "Atualizar")
        }
    }
}

@Composable
fun PostItem(post: Post, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.title, style = MaterialTheme.typography.titleMedium)
            Text(text = post.body, style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onEdit) { Text("Editar") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) { Text("Excluir") }
            }
        }
    }
}

data class Post(val id: Int, val title: String, val body: String)

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AppNavigator()
}
