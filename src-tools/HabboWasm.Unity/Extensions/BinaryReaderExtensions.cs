using System.IO;
using System.Text;

namespace HabboWasm.Unity.Extensions
{
    internal static class BinaryReaderExtensions
    {
        public static string ReadStringNullTerminated(this BinaryReader reader)
        {
            var sb = new StringBuilder();

            byte num;
            while ((num = reader.ReadByte()) > (byte) 0)
            {
                sb.Append((char) num);
            }

            return sb.ToString();
        }
    }
}