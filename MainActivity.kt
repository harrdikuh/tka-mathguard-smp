package com.example.tkamathguard

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.max


data class Question(val text:String,val options:List<String>,val answer:Int)
data class StudentResult(val name:String,val correct:Int,val wrong:Int,val integrity:Int){
    val score:Int get() = correct * 5
}

class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onBackPressedDispatcher.addCallback(this,object:OnBackPressedCallback(true){
            override fun handleOnBackPressed(){}
        })
        setContent{ App(this) }
    }
}

@Composable
fun App(activity:Activity){
    var mode by remember{mutableStateOf("home")}
    var studentName by remember{mutableStateOf("")}
    var classCode by remember{mutableStateOf("SMP-9A")}
    var results by remember{mutableStateOf(sampleResults)}
    when(mode){
        "home" -> Home(
            onStudent={mode="student"},
            onTeacher={mode="teacher"}
        )
        "student" -> StudentEntry(
            name=studentName,
            onName={studentName=it},
            code=classCode,
            onCode={classCode=it},
            onStart={mode="exam"}
        )
        "exam" -> Exam(onDone={r->
            results = results + StudentResult(studentName.ifBlank{"Siswa Baru"},r.first,20-r.first,r.second)
            mode="studentDone"
        })
        "studentDone" -> StudentDone(onBack={mode="home"})
        "teacher" -> TeacherDashboard(results,onBack={mode="home"})
    }
}

@Composable fun Home(onStudent:()->Unit,onTeacher:()->Unit){
    Column(Modifier.fillMaxSize().padding(28.dp),verticalArrangement=Arrangement.Center){
        Text("TKA MathGuard SMP",style=MaterialTheme.typography.headlineLarge)
        Text("Platform ujian Matematika SMP kelas 7–9")
        Spacer(Modifier.height(24.dp))
        Button(onClick=onStudent,Modifier.fillMaxWidth()){Text("Masuk sebagai Siswa")}
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick=onTeacher,Modifier.fillMaxWidth()){Text("Dashboard Guru")}
    }
}

@Composable fun StudentEntry(name:String,onName:(String)->Unit,code:String,onCode:(String)->Unit,onStart:()->Unit){
    Column(Modifier.fillMaxSize().padding(28.dp),verticalArrangement=Arrangement.Center){
        Text("Masuk Ujian",style=MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(name,onName,Modifier.fillMaxWidth(),label={Text("Nama siswa")})
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(code,onCode,Modifier.fillMaxWidth(),label={Text("Kode kelas")})
        Spacer(Modifier.height(18.dp))
        Button(onClick=onStart,enabled=name.isNotBlank()&&code.isNotBlank(),Modifier.fillMaxWidth()){
            Text("Mulai TKA")
        }
    }
}

@Composable fun Exam(onDone:(Pair<Int,Int>)->Unit){
    var i by remember{mutableIntStateOf(0)}
    var correct by remember{mutableIntStateOf(0)}
    var left by remember{mutableIntStateOf(3600)}
    var focus by remember{mutableIntStateOf(0)}
    val qs=remember{bank.shuffled().take(20).map{q->
        val a=q.options.mapIndexed{n,s->s to(n==q.answer)}.shuffled()
        Question(q.text,a.map{it.first},a.indexOfFirst{it.second})
    }}
    LaunchedEffect(Unit){
        while(left>0){delay(1000);left--}
        if(left==0)onDone(correct to max(0,100-focus*10))
    }
    Column(Modifier.fillMaxSize().padding(22.dp)){
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
            Text("Soal ${i+1}/20")
            Text(String.format("%02d:%02d",left/60,left%60))
        }
        Spacer(Modifier.height(20.dp))
        Text(qs[i].text,style=MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(22.dp))
        qs[i].options.forEachIndexed{n,s->
            OutlinedButton({
                if(n==qs[i].answer)correct++
                if(i==19) onDone((correct+(if(n==qs[i].answer)1 else 0)) to max(0,100-focus*10))
                else i++
            },Modifier.fillMaxWidth().padding(vertical=4.dp)){Text(s)}
        }
    }
}

@Composable fun StudentDone(onBack:()->Unit){
    Column(Modifier.fillMaxSize().padding(28.dp),verticalArrangement=Arrangement.Center){
        Text("Ujian terkirim",style=MaterialTheme.typography.headlineLarge)
        Text("Hasil sudah masuk ke rekapan kelas.")
        Spacer(Modifier.height(18.dp))
        Button(onClick=onBack){Text("Selesai")}
    }
}

@Composable fun TeacherDashboard(results:List<StudentResult>,onBack:()->Unit){
    val ranked=results.sortedByDescending{it.score}
    Column(Modifier.fillMaxSize().padding(18.dp)){
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
            Column{
                Text("Dashboard Guru",style=MaterialTheme.typography.headlineMedium)
                Text("Kelas SMP-9A • ${results.size} peserta")
            }
            OutlinedButton(onClick=onBack){Text("Keluar")}
        }
        Spacer(Modifier.height(12.dp))
        val avg=if(results.isEmpty())0 else results.map{it.score}.average().toInt()
        Card(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.SpaceEvenly){
            Metric("Rata-rata","$avg")
            Metric("Benar","${results.sumOf{it.correct}}")
            Metric("Salah","${results.sumOf{it.wrong}}")
        }}
        Spacer(Modifier.height(12.dp))
        Text("Ranking Kelas",style=MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        ranked.forEachIndexed{rank,r->
            Card(Modifier.fillMaxWidth().padding(vertical=3.dp)){
                Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){
                    Text("${rank+1}. ${r.name}")
                    Column(horizontalAlignment=Alignment.End){
                        Text("${r.score}")
                        Text("${r.correct} benar • ${r.wrong} salah • I:${r.integrity}")
                    }
                }
            }
        }
    }
}
@Composable fun Metric(label:String,value:String){
    Column(horizontalAlignment=Alignment.CenterHorizontally){
        Text(value,style=MaterialTheme.typography.titleLarge)
        Text(label)
    }
}

val sampleResults=listOf(
    StudentResult("Andi Pratama",18,2,100),
    StudentResult("Budi Saputra",17,3,100),
    StudentResult("Citra Lestari",16,4,90),
    StudentResult("Dina Putri",15,5,100),
    StudentResult("Eko Ramadhan",14,6,90)
)

val bank=listOf(
Question("Jika 3x − 7 = 20, nilai x adalah …",listOf("7","8","9","10"),2),
Question("Sederhanakan 4a + 7a − 3a.",listOf("6a","7a","8a","9a"),2),
Question("Hasil dari (−8) + 13 − 6 adalah …",listOf("−1","1","5","11"),0),
Question("2/3 + 3/4 = …",listOf("5/7","13/12","17/12","19/12"),1),
Question("Jika 5 buku seharga Rp40.000, harga 8 buku adalah …",listOf("Rp56.000","Rp60.000","Rp64.000","Rp72.000"),2),
Question("Gradien garis melalui (0,2) dan (3,8) adalah …",listOf("1","2","3","6"),1),
Question("Luas lingkaran berjari-jari 7 cm (π=22/7) adalah …",listOf("44","88","154","308"),2),
Question("Sebuah segitiga alas 14 cm dan tinggi 9 cm. Luasnya …",listOf("63","72","126","252"),0),
Question("Jika 20% dari x = 36, maka x = …",listOf("120","160","180","200"),2),
Question("Rata-rata 6, 8, 10, 12, 14 adalah …",listOf("8","9","10","11"),2),
Question("Peluang muncul bilangan genap saat melempar dadu adalah …",listOf("1/6","1/3","1/2","2/3"),2),
Question("Volume balok 8 cm × 5 cm × 4 cm adalah …",listOf("120","140","160","180"),2),
Question("FPB dari 48 dan 72 adalah …",listOf("12","18","24","36"),2),
Question("KPK dari 12 dan 18 adalah …",listOf("24","30","36","54"),2),
Question("Jika y = 2x + 3 dan x=5, maka y = …",listOf("10","11","12","13"),3),
Question("Perbandingan siswa laki-laki : perempuan = 3:5. Jika total 32 siswa, jumlah perempuan …",listOf("12","18","20","24"),2),
Question("Sisi miring segitiga siku-siku dengan kaki 6 cm dan 8 cm adalah …",listOf("9","10","12","14"),1),
Question("Harga Rp150.000 didiskon 20%. Harga akhirnya …",listOf("Rp110.000","Rp120.000","Rp125.000","Rp130.000"),1),
Question("Bentuk faktorisasi x² + 5x + 6 adalah …",listOf("(x+1)(x+6)","(x+2)(x+3)","(x−2)(x−3)","(x+5)(x+1)"),1),
Question("Median dari 4, 7, 9, 12, 15, 18, 20 adalah …",listOf("9","12","15","18"),1),
Question("Jika 3/5 suatu bilangan = 24, bilangan tersebut …",listOf("30","36","40","45"),2),
Question("Keliling persegi panjang panjang 12 cm dan lebar 7 cm adalah …",listOf("19","26","38","84"),2),
Question("Nilai dari 2⁴ + 3² adalah …",listOf("17","20","23","25"),2),
Question("Persamaan 2(x+3)=18 memiliki x = …",listOf("5","6","7","8"),0),
Question("Sebuah prisma memiliki luas alas 20 cm² dan tinggi 9 cm. Volumenya …",listOf("90","120","180","200"),2),
Question("Jika 4 : x = 2 : 7, maka x = …",listOf("7","10","12","14"),3),
Question("Panjang sisi persegi jika luasnya 144 cm² adalah …",listOf("10","11","12","14"),2),
Question("Suku berikutnya 3, 7, 11, 15, … adalah …",listOf("17","18","19","20"),2),
Question("Jika 2x + y = 11 dan x=4, maka y = …",listOf("2","3","4","5"),1),
Question("Sebuah data memiliki modus 8. Artinya …",listOf("8 adalah nilai tengah","8 paling sering muncul","8 nilai terbesar","8 nilai terkecil"),1)
)
