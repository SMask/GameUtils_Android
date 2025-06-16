package com.mask.gameutils.module.energyBlast.config

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * 装备词条 Json 解析器
 *
 * Create by lishilin on 2025-06-16
 */
class EnergyBlastAffixTypeAdapter : TypeAdapter<IEnergyBlastAffix>() {

    override fun write(jsonWriter: JsonWriter?, value: IEnergyBlastAffix?) {
        if (jsonWriter == null || value == null) {
            return
        }
        when (value) {
            is EnergyBlastStatAffix -> {
                jsonWriter.value(value.name)
            }

            is EnergyBlastSkillAffix -> {
                jsonWriter.value(value.name)
            }
        }
    }

    override fun read(jsonReader: JsonReader?): IEnergyBlastAffix {
        if (jsonReader == null) {
            throw IOException("jsonReader 为 null")
        }
        val name = jsonReader.nextString()
        try {
            return EnergyBlastStatAffix.valueOf(name)
        } catch (_: Exception) {
        }
        try {
            return EnergyBlastSkillAffix.valueOf(name)
        } catch (_: Exception) {
        }
        throw IOException("装备词条解析错误，请检查对比数据")
    }
}