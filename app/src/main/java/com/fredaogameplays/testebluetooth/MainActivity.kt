package com.fredaogameplays.testebluetooth

import android.annotation.TargetApi
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity(){

    var socketBt : BluetoothSocket? = null
    var myBtadpater : BluetoothAdapter? = null
    val nDaActivity = 0
    val nDaConexao = 1
    var conexao = false
    var MAC: String? = ""
    var UUID_default : UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    var Btconexao : ConnectedThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ledbtn = findViewById<Button>(R.id.ledbtn)
        val led2btn = findViewById<Button>(R.id.led2btn)
        val led3btn = findViewById<Button>(R.id.led3btn)
        val conectbtn = findViewById<Button>(R.id.conectbtn)
        val BtStatusText = findViewById<TextView>(R.id.BtStatusTxt)
        myBtadpater  = BluetoothAdapter.getDefaultAdapter()


        if(myBtadpater == null){
            Toast.makeText(applicationContext, "Sem bluetooth irmao", Toast.LENGTH_LONG).show()
        }else if(!myBtadpater!!.isEnabled){
            val abilitarBt = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(abilitarBt, nDaActivity)
            Toast.makeText(applicationContext, "LIGA O bluetooth BURRO", Toast.LENGTH_LONG).show()
        }




        conectbtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v : View){
                if(conexao){
                    //DESCONECTAR
                    try{
                        socketBt!!.close()
                        conexao = false
                        BtStatusText.text = getString(R.string.status_des_txt)
                        BtStatusText.setTextColor(getColor(R.color.status_des))
                        conectbtn.text = "Conectar"
                    }catch (erro: IOException){
                        Toast.makeText(applicationContext, "ocorreu um erro: $erro", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    val abrirLista : Intent = Intent(this@MainActivity, ListaDispositivos::class.java)
                    startActivityForResult(abrirLista, nDaConexao)
                }
            }
        })

        ledbtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v : View){
                if(conexao){
                    Btconexao!!.write("a")
                }else{
                    Toast.makeText(applicationContext, "Conecte o Bluetooth", Toast.LENGTH_SHORT).show()
                }
            }
        })

        led2btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if(conexao){
                    Btconexao!!.write("b")
                }else{
                    Toast.makeText(applicationContext, "Conecte o Bluetooth", Toast.LENGTH_SHORT).show()
                }
            }
        })

        led3btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if(conexao){
                    Btconexao!!.write("c")
                }else{
                    Toast.makeText(applicationContext, "Conecte o Bluetooth", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }


    @TargetApi(23)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            nDaActivity -> {
                if(resultCode == Activity.RESULT_CANCELED){
                    finish()
                    moveTaskToBack(true)
                }
            }
            nDaConexao -> {
                if(resultCode == Activity.RESULT_OK){
                    MAC = data!!.getStringExtra("MAC")
                    Toast.makeText(applicationContext, "obteve o mac $MAC", Toast.LENGTH_SHORT).show()
                    //Toast.makeText(applicationContext, "$UUID_default", Toast.LENGTH_SHORT).show()
                    try{
                        val myBtDevice : BluetoothDevice = myBtadpater!!.getRemoteDevice(MAC)
                        socketBt =  myBtDevice.createRfcommSocketToServiceRecord(UUID_default)
                        socketBt!!.connect()
                        Toast.makeText(applicationContext, "coniquitadu com $MAC", Toast.LENGTH_SHORT).show()
                        conexao = true
                        BtStatusTxt.text = getString(R.string.status_con_txt)
                        BtStatusTxt.setTextColor(getColor(R.color.status_con))
                        Btconexao = ConnectedThread()

                        conectbtn.text = "Desconectar"
                    }catch (erro : IOException){
                        conexao = false
                        Toast.makeText(applicationContext, "erro $erro", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(applicationContext, "falha ao obter o mac", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    inner class ConnectedThread : Thread() {
        private val recebimento : InputStream = socketBt!!.inputStream
        private val envio : OutputStream = socketBt!!.outputStream
        fun write(info : String) {
            var msg : ByteArray = info.toByteArray()
            try {
                envio.write(msg)
            } catch (e: IOException) {
                return
            }
            // Share the sent message with the UI activity.
        }
    }
}

