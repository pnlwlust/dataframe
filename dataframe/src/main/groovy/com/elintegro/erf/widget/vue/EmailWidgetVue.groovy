/* Elintegro Dataframe is a framework designed to accelerate the process of full-stack application development. 
We invite you to join the community of developers making it even more powerful!
For more information please visit  https://www.elintegro.com

Copyright © 2007-2019  Elinegro Inc. Eugene Lipkovich, Shai Lipkovich

This program is under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
You are not required to accept this License, since you have not signed it. However, nothing else grants you permission to modify or distribute the Program or its derivative works. 
These actions are prohibited by law if you do not accept this License. Therefore, by modifying or distributing the Program or any work based on the Program, you indicate your acceptance of this License to do so, and all its terms and conditions for copying, distributing or modifying the Program or works based on it. */


package com.elintegro.erf.widget.vue

import com.elintegro.erf.dataframe.vue.DataframeVue
import grails.util.Holders

import org.springframework.context.i18n.LocaleContextHolder
/**
 * Created by kchapagain on Oct, 2018.
 */
class EmailWidgetVue extends WidgetVue{

    @Override
    String getHtml(DataframeVue dataframe, Map field) {
        String fldName = dataframe.getDataVariableForVue(field)
        boolean isReadOnly = dataframe.isReadOnly(field)
        def width = field.width?:'auto'
        def height = field.hight?:'30px'
        String label = field.label
        return """
               <v-text-field
                 label="$label"
                 v-model="$fldName"
                 :rules = "${fldName}_rule"
                 ${isReadOnly?"readonly":''}
                 ${toolTip(field)}
                 style="width:$width; height:$height;"   
                ></v-text-field>
               """
    }
    String getVueSaveVariables(DataframeVue dataframe, Map field){
        String thisFieldName = dataframe.getFieldId(field)
        String dataVariable = dataframe.getDataVariableForVue(field)
        return """allParams['$thisFieldName'] = this.$dataVariable;\n allParams['contactEmail'] = this.$dataVariable;\n"""
    }
    @Override
    String getValueSetter(DataframeVue dataframe, Map field, String divId, String dataVariable, String key) {
        String errorMessageCode = field.errMessageCode?:"email.validation.message"
        def emailRegex = Holders.grailsApplication.config.regex.email
        String errorMessage = getMessageSource().getMessage(errorMessageCode, null, errorMessageCode, LocaleContextHolder.getLocale())
        if (emailRegex){
            field.put("regex",emailRegex)
            String regex = "/${emailRegex}/"
            if (field?.validate){
                def rule = field.validate.rule
                rule.add('(v) => '+regex+".test(v) || '$errorMessage'")
            }else {
                field << ["validate":["rule":['(v) => '+regex+".test(v) || '$errorMessage'"]]]
            }
        }
        String vueInstance = dataframe.dataframeName+"_instance"
        return """this.$dataVariable = response['$key'];"""
    }
}
