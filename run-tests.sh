#!/usr/bin/env bash
# Compile dan jalankan semua test case (TC-01..TC-04) untuk setiap studi kasus.
# Setiap baris di TC-0N.expected.txt harus muncul (berurutan) pada output program.
cd "$(dirname "$0")" || exit 1

pass=0; fail=0
for proj in catatan-keuangan buku-tamu inventaris-barang jadwal-kegiatan kontak-teman; do
  echo "== $proj =="
  rm -rf "$proj/output"
  if ! javac -d "$proj/output" -sourcepath "$proj/src" "$proj/src/App.java"; then
    echo "  [GAGAL] compile"; fail=$((fail+1)); continue
  fi
  for tc in "$proj"/test-cases/TC-0?.tc; do
    name=$(basename "$tc" .tc)
    expected="$proj/test-cases/$name.expected.txt"
    actual=$(mktemp)
    java -cp "$proj/output" App < "$tc" > "$actual"
    ok=1; pos=1
    while IFS= read -r line || [ -n "$line" ]; do
      [ -z "$line" ] && continue
      found=$(tail -n +"$pos" "$actual" | grep -n -F -m1 -- "$line" | cut -d: -f1)
      if [ -z "$found" ]; then
        echo "    tidak ditemukan: $line"; ok=0
      else
        pos=$((pos + found))
      fi
    done < "$expected"
    if [ $ok -eq 1 ]; then echo "  [LULUS] $name"; pass=$((pass+1))
    else echo "  [GAGAL] $name"; fail=$((fail+1)); fi
    rm -f "$actual"
  done
done
echo "Ringkasan: $pass lulus, $fail gagal"
[ $fail -eq 0 ]
