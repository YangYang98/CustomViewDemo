package com.yang.customviewdemo.ui.activity.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tanjiajun.jsonrecyclerview.view.JSONRecyclerView
import com.yang.customviewdemo.R


/**
 * Create by Yang Yang on 2023/6/26
 */
class TestJsonRecyclerViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_json_recycler_view)

        /*findViewById<JSONRecyclerView>(R.id.rv_json).bindData(
            "{\n" +
                    "    \"string\":\"string\",\n" +
                    "    \"number\":100,\n" +
                    "    \"boolean\":true,\n" +
                    "    \"url\":\"https://github.com/TanJiaJunBeyond/JSONRecyclerView\",\n" +
                    "    \"JSONObject\":{\n" +
                    "        \"string\":\"string\",\n" +
                    "        \"number\":100,\n" +
                    "        \"boolean\":true\n" +
                    "    },\n" +
                    "    \"JSONArray\":[\n" +
                    "        {\n" +
                    "            \"string\":\"string\",\n" +
                    "            \"number\":100,\n" +
                    "            \"boolean\":true\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}"
        )*/
        findViewById<JSONRecyclerView>(R.id.rv_json).bindData(
            "{\"domain\":{\"chainId\":\"5\",\"name\":\"Ether Mail\",\"verifyingContract\":\"0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC\",\"version\":\"1\"},\"message\":{\"from\":{\"name\":\"Cow\",\"wallets\":[\"0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826\",\"0xDeaDbeefdEAdbeefdEadbEEFdeadbeEFdEaDbeeF\"]},\"to\":[{\"name\":\"Bob\",\"wallets\":[\"0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB\",\"0xB0BdaBea57B0BDABeA57b0bdABEA57b0BDabEa57\",\"0xB0B0b0b0b0b0B000000000000000000000000000\"]}],\"contents\":\"Hello, Bob!\"},\"primaryType\":\"Mail\",\"types\":{\"EIP712Domain\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"},{\"name\":\"chainId\",\"type\":\"uint256\"},{\"name\":\"verifyingContract\",\"type\":\"address\"}],\"Group\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"members\",\"type\":\"Person[]\"}],\"Mail\":[{\"name\":\"from\",\"type\":\"Person\"},{\"name\":\"to\",\"type\":\"Person[]\"},{\"name\":\"contents\",\"type\":\"string\"}],\"Person\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"wallets\",\"type\":\"address[]\"}]}}"
        )
    }
}