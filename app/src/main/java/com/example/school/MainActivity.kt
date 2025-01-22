package com.example.school

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.school.room_database.Event
import com.example.school.ui.theme.SchoolTheme

import org.json.JSONArray

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchoolTheme {
                AppNavigator()
            }
        }
    }
}




@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    var userList by remember { mutableStateOf(emptyList<User>()) }


    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController = navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("home") { HomeScreen(navController = navController) }
        composable("search_screen") {
            SearchScreen(userList = userList)
        }
    }
}



@Composable
fun SearchScreen(userList: List<User>) {
    var searchQuery by remember { mutableStateOf("") }
    var filteredUsers by remember { mutableStateOf(emptyList<User>()) }

    fun filterUsers(query: String) {
        filteredUsers = if (query.isEmpty()) {
            emptyList()
        } else {
            userList.filter { user ->
                user.name.contains(query, ignoreCase = true) ||
                        user.email.contains(query, ignoreCase = true) ||
                        user.username.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Pesquisar usuário",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                filterUsers(it)
            },
            label = { Text("Digite o nome, e-mail ou username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredUsers.isEmpty() && searchQuery.isNotEmpty()) {
            Text("Nenhum usuário encontrado.", style = MaterialTheme.typography.bodyMedium)
        } else if (filteredUsers.isNotEmpty()) {
            LazyColumn {
                items(filteredUsers) { user ->
                    UserItems(user)
                }
            }
        }
    }
}

@Composable
fun UserItems(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Username: ${user.username}", style = MaterialTheme.typography.bodySmall)
        }
    }
}



@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        navController.navigate("login")
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Bem-vindo ao Gestor de usuários", style = MaterialTheme.typography.titleLarge)
    }
}



@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuário") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Senha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (username == "admin" && password == "1234") {
                    navController.navigate("home")
                } else {
                    showError = true
                }
            }) {
                Text("Entrar")
            }

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Usuário ou senha inválidos", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val requestQueue = Volley.newRequestQueue(context)

    var userList by remember { mutableStateOf(emptyList<User>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val url = "https://jsonplaceholder.typicode.com/users"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val users = mutableListOf<User>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val user = User(
                            id = jsonObject.getInt("id"),
                            name = jsonObject.getString("name"),
                            username = jsonObject.getString("username"),
                            email = jsonObject.getString("email"),
                            phone = jsonObject.getString("phone"),
                            website = jsonObject.getString("website")
                        )
                        users.add(user)
                    }
                    userList = users
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        isLoading = false
                    }
                }
            },
            { error ->
                error.printStackTrace()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    isLoading = false
                }
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Usuários - Home") })
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column {
                    Text(
                        text = "Bem-vindo! Aqui estão alguns usuários:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    Button(
                        onClick = { navController.navigate("search_screen") },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Pesquisar Usuários")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(userList) { user ->
                            UserItem(user)
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    )
}

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String
)

@Composable
fun UserItem(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Nome: ${user.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Usuário: ${user.username}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Telefone: ${user.phone}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Website: ${user.website}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun AppPreview() {
    SchoolTheme {
        AppNavigator()
    }
}
