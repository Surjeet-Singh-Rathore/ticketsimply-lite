#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include <android/log.h>


JNIEXPORT jstring JNICALL
Java_com_bitla_ts_utils_security_EncrypDecryp_getApiKey(JNIEnv *env, jobject instance) {
    return (*env)-> NewStringUTF(env, "AIzaSyC4R74auuaVepPwAhDXG6wY15vsdKO8D-s");
}

// Base64 encoding and decoding functions
static const char encoding_table[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
static const char decoding_table[256] = {
        [0 ... 255] = -1, ['A'] = 0, ['B'] = 1, ['C'] = 2, ['D'] = 3, ['E'] = 4, ['F'] = 5, ['G'] = 6, ['H'] = 7,
        ['I'] = 8, ['J'] = 9, ['K'] = 10, ['L'] = 11, ['M'] = 12, ['N'] = 13, ['O'] = 14, ['P'] = 15, ['Q'] = 16,
        ['R'] = 17, ['S'] = 18, ['T'] = 19, ['U'] = 20, ['V'] = 21, ['W'] = 22, ['X'] = 23, ['Y'] = 24, ['Z'] = 25,
        ['a'] = 26, ['b'] = 27, ['c'] = 28, ['d'] = 29, ['e'] = 30, ['f'] = 31, ['g'] = 32, ['h'] = 33, ['i'] = 34,
        ['j'] = 35, ['k'] = 36, ['l'] = 37, ['m'] = 38, ['n'] = 39, ['o'] = 40, ['p'] = 41, ['q'] = 42, ['r'] = 43,
        ['s'] = 44, ['t'] = 45, ['u'] = 46, ['v'] = 47, ['w'] = 48, ['x'] = 49, ['y'] = 50, ['z'] = 51, ['0'] = 52,
        ['1'] = 53, ['2'] = 54, ['3'] = 55, ['4'] = 56, ['5'] = 57, ['6'] = 58, ['7'] = 59, ['8'] = 60, ['9'] = 61,
        ['+'] = 62, ['/'] = 63
};
static const int mod_table[] = {0, 2, 1};

/**
 * Encryption
 * @param data
 * @param input_length
 * @param output_length
 * @return
 */
char* base64_encode(const unsigned char* data, size_t input_length, size_t* output_length) {
    *output_length = 4 * ((input_length + 2) / 3);

    char* encoded_data = (char*)malloc(*output_length + 1);
    if (encoded_data == NULL) return NULL;

    for (size_t i = 0, j = 0; i < input_length;) {
        uint32_t octet_a = i < input_length ? (unsigned char) data[i++] : 0;
        uint32_t octet_b = i < input_length ? (unsigned char) data[i++] : 0;
        uint32_t octet_c = i < input_length ? (unsigned char) data[i++] : 0;

        uint32_t triple = (octet_a << 0x10) + (octet_b << 0x08) + octet_c;

        encoded_data[j++] = encoding_table[(triple >> 3 * 6) & 0x3F];
        encoded_data[j++] = encoding_table[(triple >> 2 * 6) & 0x3F];
        encoded_data[j++] = encoding_table[(triple >> 1 * 6) & 0x3F];
        encoded_data[j++] = encoding_table[(triple >> 0 * 6) & 0x3F];
    }

    for (size_t i = 0; i < mod_table[input_length % 3]; i++)
        encoded_data[*output_length - 1 - i] = '=';

    encoded_data[*output_length] = '\0';
    return encoded_data;
}

char* append_after_every_two_chars(const char* input, const char* appendString) {
    size_t input_length = strlen(input);
    size_t append_length = strlen(appendString);
    size_t result_length = input_length + (input_length / 2) * append_length;

    char* result = (char*)malloc(result_length + 1);
    if (result == NULL) {
        return NULL;
    }

    size_t j = 0;
    for (size_t i = 0; i < input_length; i++) {
        result[j++] = input[i];
        if ((i + 1) % 2 == 0 && i != input_length - 1) {
            strncpy(result + j, appendString, append_length);
            j += append_length;
        }
    }
    result[j] = '\0';
    return result;
}

JNIEXPORT jstring JNICALL
Java_com_bitla_ts_utils_security_EncrypDecryp_encodeToBase64(JNIEnv *env, jobject obj, jstring jInput) {
    // Step 1: Base64 encode "TSB"
    const char *ticketSimply = "TSB";
    size_t ticket_output_length;
    char *base64TicketSimply = base64_encode((const unsigned char*)ticketSimply, strlen(ticketSimply), &ticket_output_length);
    if (base64TicketSimply == NULL) {
        return NULL;
    }

    // Step 2: Append base64TicketSimply after every two characters in the input string
    const char *input = (*env)->GetStringUTFChars(env, jInput, NULL);
    if (input == NULL) {
        free(base64TicketSimply);
        return NULL;
    }

    char *appended = append_after_every_two_chars(input, base64TicketSimply);
    (*env)->ReleaseStringUTFChars(env, jInput, input);
    free(base64TicketSimply);
    if (appended == NULL) {
        return NULL;
    }

    // Step 3: Base64 encode the resulting string again
    size_t final_output_length;
    char *finalBase64Encoded = base64_encode((const unsigned char*)appended, strlen(appended), &final_output_length);
    free(appended);
    if (finalBase64Encoded == NULL) {
        return NULL;
    }

    jstring result = (*env)->NewStringUTF(env, finalBase64Encoded);
    free(finalBase64Encoded);
    return result;
}


/**
 * Decryption
 * @param data
 * @param input_length
 * @param output_length
 * @return
 */
unsigned char* base64_decode(const char* data, size_t input_length, size_t* output_length) {
    if (input_length % 4 != 0) return NULL;

    *output_length = input_length / 4 * 3;
    if (data[input_length - 1] == '=') (*output_length)--;
    if (data[input_length - 2] == '=') (*output_length)--;

    unsigned char* decoded_data = (unsigned char*)malloc(*output_length);
    if (decoded_data == NULL) return NULL;

    for (size_t i = 0, j = 0; i < input_length;) {
        uint32_t sextet_a = data[i] == '=' ? 0 & i++ : decoding_table[(unsigned char)data[i++]];
        uint32_t sextet_b = data[i] == '=' ? 0 & i++ : decoding_table[(unsigned char)data[i++]];
        uint32_t sextet_c = data[i] == '=' ? 0 & i++ : decoding_table[(unsigned char)data[i++]];
        uint32_t sextet_d = data[i] == '=' ? 0 & i++ : decoding_table[(unsigned char)data[i++]];

        uint32_t triple = (sextet_a << 3 * 6) + (sextet_b << 2 * 6) + (sextet_c << 1 * 6) + (sextet_d << 0 * 6);

        if (j < *output_length) decoded_data[j++] = (triple >> 2 * 8) & 0xFF;
        if (j < *output_length) decoded_data[j++] = (triple >> 1 * 8) & 0xFF;
        if (j < *output_length) decoded_data[j++] = (triple >> 0 * 8) & 0xFF;
    }

    return decoded_data;
}

JNIEXPORT jstring JNICALL
Java_com_bitla_ts_utils_security_EncrypDecryp_decodeFromBase64(JNIEnv *env, jobject obj, jstring jEncoded) {
    const char *encoded = (*env)->GetStringUTFChars(env, jEncoded, NULL);
    if (encoded == NULL) {
        return NULL;
    }

    // Step 1: Base64 decode the encoded string
    size_t decoded_length;
    unsigned char *decoded = base64_decode(encoded, strlen(encoded), &decoded_length);
    (*env)->ReleaseStringUTFChars(env, jEncoded, encoded);
    if (decoded == NULL) {
        return NULL;
    }

    // Step 2: Remove the "TicketSimply" encoded value from the decoded string
    const char *ticketSimply = "TicketSimply";
    size_t ticket_output_length;
    const char *ticketSimplyEncoded = base64_encode((const unsigned char*)ticketSimply, strlen(ticketSimply), &ticket_output_length); // Replace with actual encoded value
    size_t ticketSimplyEncodedLength = strlen(ticketSimplyEncoded);

    char *originalEncoded = (char*)malloc(decoded_length + 1);
    if (originalEncoded == NULL) {
        free(decoded);
        return NULL;
    }

    size_t j = 0;
    for (size_t i = 0; i < decoded_length; ++i) {
        if (strncmp((char*)(decoded + i), ticketSimplyEncoded, ticketSimplyEncodedLength) == 0) {
            i += ticketSimplyEncodedLength - 1;
        } else {
            originalEncoded[j++] = decoded[i];
        }
    }
    originalEncoded[j] = '\0';
    free(decoded);

    // Step 3: Convert the original encoded string to jstring and return
    jstring result = (*env)->NewStringUTF(env, originalEncoded);
    free(originalEncoded);

    return result;
}