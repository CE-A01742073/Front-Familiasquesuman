package com.familiasquesuman.demo.data

data class SeccionAviso(val titulo: String?, val parrafos: List<String>)

object AvisoPrivacidad {
    const val ACTUALIZACION = "Última actualización: julio 2026"

    val secciones = listOf(
        SeccionAviso(
            null,
            listOf(
                "En cumplimiento con la Ley Federal de Protección de Datos Personales en Posesión de los Particulares y demás disposiciones aplicables, Familias que Suman, con domicilio en Monterrey, pone a disposición el presente Aviso de Privacidad para informar la forma en que recaba, utiliza, protege y trata los datos personales de las personas que utilizan la plataforma www.familiasquesuman.com, participan en nuestras actividades, realizan donaciones en especie, colaboran como voluntarios o mantienen contacto con la asociación."
            )
        ),
        SeccionAviso(
            "1. Responsable del tratamiento de los datos personales",
            listOf(
                "Familias que Suman es responsable del tratamiento de los datos personales que recaba y se compromete a proteger su privacidad mediante medidas administrativas, técnicas y físicas razonables para evitar su pérdida, uso indebido, acceso no autorizado, alteración o divulgación.",
                "Para cualquier asunto relacionado con este Aviso de Privacidad puede escribir a: hola@familiasquesuman.com"
            )
        ),
        SeccionAviso(
            "2. Datos personales que podemos recabar",
            listOf(
                "Dependiendo de la relación que tenga con Familias que Suman, podremos solicitar alguno o varios de los siguientes datos:",
                "• Nombre completo.\n• Número telefónico.\n• Correo electrónico.\n• Ciudad o municipio de residencia.\n• Información sobre su participación como voluntario o donante.\n• Nombre y edad de los menores que participarán en actividades familiares, cuando sea necesario para su inscripción.\n• Fotografías y videos obtenidos durante actividades organizadas o coordinadas por Familias que Suman, cuando exista la autorización correspondiente.\n• Cualquier otra información que usted decida proporcionarnos mediante formularios de contacto, registro o comunicación directa.",
                "Familias que Suman no solicita datos personales sensibles de manera habitual. En caso de que excepcionalmente sea necesario recabarlos para una actividad específica, se solicitará el consentimiento correspondiente conforme a la legislación aplicable."
            )
        ),
        SeccionAviso(
            "3. Finalidades para las que utilizamos sus datos",
            listOf(
                "Los datos personales serán utilizados para las siguientes finalidades principales:",
                "• Registrar a familias, voluntarios y participantes en actividades.\n• Coordinar campañas, proyectos y acciones solidarias.\n• Dar seguimiento a inscripciones y participación en actividades.\n• Contactar a donantes, voluntarios e instituciones cuando sea necesario.\n• Informar sobre cambios, horarios, ubicaciones o aspectos relacionados con las actividades.\n• Gestionar la participación de instituciones aliadas.\n• Facilitar la vinculación entre familias e instituciones participantes cuando así corresponda.\n• Mantener comunicación relacionada con los servicios y actividades de Familias que Suman.\n• Atender dudas, solicitudes o comentarios recibidos por cualquier medio.",
                "Adicionalmente, podremos utilizar sus datos para las siguientes finalidades secundarias:",
                "• Enviar información sobre nuevas campañas, proyectos y oportunidades de voluntariado.\n• Compartir noticias, resultados e impacto de las actividades realizadas.\n• Invitar a eventos organizados por Familias que Suman.\n• Elaborar estadísticas internas que permitan mejorar nuestros programas y servicios.",
                "Si usted no desea que sus datos sean utilizados para estas finalidades secundarias, podrá solicitarlo en cualquier momento escribiendo a hola@familiasquesuman.com."
            )
        ),
        SeccionAviso(
            "4. Fotografías y videos",
            listOf(
                "Durante algunas actividades podrán tomarse fotografías o videos con fines informativos, institucionales o de difusión.",
                "Cuando sea necesario, Familias que Suman solicitará la autorización correspondiente para el uso de imagen.",
                "En actividades realizadas conjuntamente con instituciones participantes, el uso de fotografías de beneficiarios estará sujeto a las autorizaciones y políticas de la institución correspondiente."
            )
        ),
        SeccionAviso(
            "5. Transferencia de datos personales",
            listOf(
                "Familias que Suman podrá compartir únicamente la información necesaria con instituciones participantes cuando ello sea indispensable para coordinar la inscripción o participación en una actividad específica.",
                "Fuera de estos casos, sus datos personales no serán vendidos, comercializados ni compartidos con terceros para fines publicitarios.",
                "Las transferencias que, en su caso, se realicen, se limitarán a aquellas permitidas por la legislación aplicable o a las que usted haya autorizado."
            )
        ),
        SeccionAviso(
            "6. Derechos ARCO",
            listOf(
                "Usted podrá ejercer en cualquier momento sus derechos de Acceso, Rectificación, Cancelación y Oposición.",
                "Asimismo, podrá revocar el consentimiento otorgado para el tratamiento de sus datos cuando ello sea legalmente procedente.",
                "Para ejercer cualquiera de estos derechos deberá enviar una solicitud al correo hola@familiasquesuman.com. La solicitud deberá incluir:",
                "• Nombre completo.\n• Medio para recibir respuesta.\n• Descripción clara del derecho que desea ejercer.\n• En su caso, documentos que permitan acreditar su identidad o representación legal."
            )
        ),
        SeccionAviso(
            "7. Uso de cookies y tecnologías similares",
            listOf(
                "Nuestro sitio web podrá utilizar cookies u otras tecnologías para mejorar la experiencia de navegación, conocer estadísticas de uso y optimizar el funcionamiento de la plataforma.",
                "Estas tecnologías no recopilan información personal sin su consentimiento y pueden deshabilitarse desde la configuración de su navegador."
            )
        ),
        SeccionAviso(
            "8. Conservación y seguridad de la información",
            listOf(
                "Familias que Suman conservará los datos personales únicamente durante el tiempo necesario para cumplir con las finalidades descritas en este Aviso de Privacidad y adoptará medidas razonables para protegerlos contra acceso, uso o divulgación no autorizados."
            )
        ),
        SeccionAviso(
            "9. Menores de edad",
            listOf(
                "Familias que Suman promueve actividades familiares y, en algunos casos, podrá recabar información relacionada con menores de edad para gestionar su participación.",
                "La información de menores será proporcionada por quien ejerza la patria potestad o tutela y será utilizada únicamente para los fines relacionados con la actividad correspondiente."
            )
        ),
        SeccionAviso(
            "10. Cambios al Aviso de Privacidad",
            listOf(
                "Familias que Suman podrá modificar o actualizar el presente Aviso de Privacidad cuando sea necesario para cumplir con cambios legales, operativos o internos.",
                "Las modificaciones serán publicadas en www.familiasquesuman.com. Se recomienda revisar periódicamente este documento para conocer cualquier actualización."
            )
        ),
        SeccionAviso(
            "11. Contacto",
            listOf(
                "Si tiene cualquier duda relacionada con el tratamiento de sus datos personales o con este Aviso de Privacidad, puede escribirnos a: hola@familiasquesuman.com",
                "Agradecemos la confianza depositada en Familias que Suman. Nuestro compromiso es proteger su información y utilizarla de manera responsable para seguir conectando familias con causas reales, confiables y puntuales."
            )
        )
    )
}
